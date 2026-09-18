package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.recipe.DryingRecipe;
import net.micaxs.smokeleaf.recipe.DryingRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.DryerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DryerBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot != INPUT_SLOT) return false;
            if (stack.isEmpty() || level == null) return false;

            return level.getRecipeManager()
                    .getRecipeFor(ModRecipes.DRYING_TYPE.get(), new DryingRecipeInput(stack), level)
                    .filter(r -> acceptsForMachine(r.value()))
                    .isPresent();
        }
    };

    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return ExtractRestrictedItemHandler.outputOnly(this.itemHandler, OUTPUT_SLOT);
    }

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 38;

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                if (getLevel() != null) {
                    getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
                }
            }
        };
    }

    public DryerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DRYER_BE.get(), pos, blockState);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> DryerBlockEntity.this.progress;
                    case 1 -> DryerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> DryerBlockEntity.this.progress = value;
                    case 1 -> DryerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public ItemStack getRenderStack() {
        if (itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return itemHandler.getStackInSlot(INPUT_SLOT);
        } else {
            return itemHandler.getStackInSlot(OUTPUT_SLOT);
        }
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Dryer");
    }


    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new DryerMenu(i, inventory, this, this.data);
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level == null || level.isClientSide()) return;

        boolean hasEnergy = this.ENERGY_STORAGE.getEnergyStored() > 0;
        Optional<RecipeHolder<DryingRecipe>> recipeOpt = getCurrentRecipe();

        Optional<ItemStack> plannedOutput = recipeOpt.flatMap(this::getPlannedOutput);
        boolean canOutput = plannedOutput.filter(this::canInsertIntoOutput).isPresent();

        if (hasEnergy && recipeOpt.isPresent() && canOutput) {
            this.maxProgress = (int) Math.max(1, recipeOpt.get().value().time() / 3);

            increaseCraftingProgress();
            this.ENERGY_STORAGE.extractEnergy(20, false);

            if (level.random.nextInt(2) == 0) {
                double x = blockPos.getX() + 0.5;
                double y = blockPos.getY() + 1.0;
                double z = blockPos.getZ() + 0.5;
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }

            setChanged(level, blockPos, blockState);

            if (hasProgressFinished()) {
                craftItem(recipeOpt.get(), plannedOutput.get());
                resetProgress();
            }
        } else {
            resetProgress();
            setChanged(level, blockPos, blockState);
        }

        boolean shouldBePowered = (progress > 0) || (hasEnergy && recipeOpt.isPresent() && canOutput);
        if (getBlockState().getValue(BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(getBlockPos(),
                    getBlockState().setValue(BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    private boolean acceptsForMachine(DryingRecipe recipe) {
        return !recipe.result().isEmpty() || recipe.dryBud();
    }

    private Optional<RecipeHolder<DryingRecipe>> getCurrentRecipe() {
        if (this.level == null) return Optional.empty();
        ItemStack in = itemHandler.getStackInSlot(INPUT_SLOT);
        if (in.isEmpty()) return Optional.empty();

        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.DRYING_TYPE.get(), new DryingRecipeInput(in), level)
                .filter(r -> acceptsForMachine(r.value()));
    }

    private Optional<ItemStack> getPlannedOutput(RecipeHolder<DryingRecipe> recipeHolder) {
        ItemStack in = itemHandler.getStackInSlot(INPUT_SLOT);
        if (in.isEmpty()) return Optional.empty();

        DryingRecipe recipe = recipeHolder.value();
        if (!recipe.result().isEmpty()) {
            ItemStack out = recipe.result().copy();
            out.setCount(Math.max(1, out.getCount()));
            return Optional.of(out);
        }

        if (recipe.dryBud()) {
            ItemStack dried = in.copy();
            dried.setCount(1);
            dried.set(ModDataComponentTypes.DRY, true);
            return Optional.of(dried);
        }

        return Optional.empty();
    }

    private boolean canInsertIntoOutput(ItemStack planned) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            return planned.getCount() <= planned.getMaxStackSize();
        }
        if (!ItemStack.isSameItemSameComponents(existing, planned)) {
            return false;
        }
        int max = Math.min(existing.getMaxStackSize(), planned.getMaxStackSize());
        return existing.getCount() + planned.getCount() <= max;
    }

    private void craftItem(RecipeHolder<DryingRecipe> recipe, ItemStack plannedOutput) {
        // Consume exactly 1 input
        itemHandler.extractItem(INPUT_SLOT, 1, false);

        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, plannedOutput.copy());
        } else if (ItemStack.isSameItemSameComponents(existing, plannedOutput)) {
            int newCount = Math.min(existing.getCount() + plannedOutput.getCount(), existing.getMaxStackSize());
            existing.setCount(newCount);
            itemHandler.setStackInSlot(OUTPUT_SLOT, existing);
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    // NBT Data
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("dryer.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("dryer.progress", progress);
        tag.putInt("dryer.maxProgress", maxProgress);
        tag.putInt("dryer.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("dryer.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("dryer.energy"));
        progress = tag.getInt("dryer.progress");
        maxProgress = tag.getInt("dryer.maxProgress");
    }

    // Server / Client Syncing
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}