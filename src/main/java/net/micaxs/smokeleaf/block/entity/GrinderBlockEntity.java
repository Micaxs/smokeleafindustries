package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.recipe.GrinderRecipe;
import net.micaxs.smokeleaf.recipe.GrinderRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.GrinderMenu;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GrinderBlockEntity extends BlockEntity implements MenuProvider {

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
                    .getRecipeFor(ModRecipes.GRINDER_TYPE.get(), new GrinderRecipeInput(stack), level)
                    .isPresent();
        }
    };

    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return this.itemHandler;
    }

    private static final int ENERGY_CONSTANT = 40;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
            }
        };
    }

    public GrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GRINDER_BE.get(), pos, blockState);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> GrinderBlockEntity.this.progress;
                    case 1 -> GrinderBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> GrinderBlockEntity.this.progress = value;
                    case 1 -> GrinderBlockEntity.this.maxProgress = value;
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
        return Component.literal("Weed Grinder");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new GrinderMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean hasEnergy = this.ENERGY_STORAGE.getEnergyStored() > 0;

        if (hasEnergy && hasRecipe()) {
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
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
            setChanged(level, blockPos, blockState);
        }

        boolean shouldBePowered = (progress > 0) || (hasEnergy && hasRecipe());
        if (getBlockState().getValue(BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(getBlockPos(),
                    getBlockState().setValue(BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<GrinderRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack assembled = recipe.get().value().assemble(
                new GrinderRecipeInput(input.copyWithCount(1)),
                level.registryAccess()
        );

        // Apply strain lineage so the component-equality check in canInsertItemIntoOutputSlot
        // matches items already in the output slot that were produced from the same strain.
        var strainData = input.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (strainData != null) assembled.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
        var strainId = input.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId != null) assembled.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

        int bonus = 0;
        if (input.getItem() instanceof BaseBudItem) {
            if (Boolean.TRUE.equals(input.get(ModDataComponentTypes.DRY))) {
                bonus = 1;
            }
        }
        int totalOut = assembled.getCount() + bonus;

        return canInsertAmountIntoOutputSlot(totalOut) && canInsertItemIntoOutputSlot(assembled);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, output);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()
                ? 64
                : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();
        return maxCount >= currentCount + count;
    }

    private Optional<RecipeHolder<GrinderRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.GRINDER_TYPE.get(), new GrinderRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private void craftItem() {
        Optional<RecipeHolder<GrinderRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return;

        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack result = recipeOpt.get().value().assemble(
                new GrinderRecipeInput(inputStack.copyWithCount(1)),
                level.registryAccess()
        );

        // Propagate strain lineage from bud to weed
        var strainData = inputStack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (strainData != null) result.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
        var strainId = inputStack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId != null) result.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

        // Apply bonus amount (+1 if bud is dry)
        int bonus = 0;
        if (inputStack.getItem() instanceof BaseBudItem) {
            if (Boolean.TRUE.equals(inputStack.get(ModDataComponentTypes.DRY))) {
                bonus = 1;
            }
        }
        int totalProduced = result.getCount() + bonus;

        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);

        // Insert result, preserving data components
        boolean inserted = false;
        if (existing.isEmpty()) {
            result.setCount(Math.min(totalProduced, result.getMaxStackSize()));
            itemHandler.setStackInSlot(OUTPUT_SLOT, result);
            inserted = true;
        } else if (ItemStack.isSameItemSameComponents(existing, result)) {
            int newCount = Math.min(existing.getCount() + totalProduced, existing.getMaxStackSize());
            existing.setCount(newCount);
            itemHandler.setStackInSlot(OUTPUT_SLOT, existing);
            inserted = true;
        }

        // Only consume input if we actually inserted the crafted item
        if (inserted) {
            itemHandler.extractItem(INPUT_SLOT, 1, false);
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
        tag.put("grinder.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("grinder.progress", progress);
        tag.putInt("grinder.maxProgress", maxProgress);
        tag.putInt("grinder.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("grinder.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("grinder.energy"));
        progress = tag.getInt("grinder.progress");
        maxProgress = tag.getInt("grinder.maxProgress");
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
