package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.ExtractorRecipe;
import net.micaxs.smokeleaf.recipe.ExtractorRecipeInput;
import net.micaxs.smokeleaf.recipe.GrinderRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.ExtractorMenu;
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

import java.util.Objects;
import java.util.Optional;

public class ExtractorBlockEntity extends BlockEntity implements MenuProvider {

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
            if (slot != INPUT_SLOT) {
                return false;
            }
            if (stack.isEmpty() || level == null) {
                return false;
            }

            return level.getRecipeManager()
                    .getRecipeFor(ModRecipes.EXTRACTOR_TYPE.get(), new ExtractorRecipeInput(stack), level)
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

    public ExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pos, blockState);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> ExtractorBlockEntity.this.progress;
                    case 1 -> ExtractorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: ExtractorBlockEntity.this.progress = value;
                    case 1: ExtractorBlockEntity.this.maxProgress = value;
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
        return Component.literal("Weed Extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ExtractorMenu(i, inventory, this, this.data);
    }

    private ItemStack buildOutputWithWeedData(ItemStack input, ItemStack recipeOutput) {
        ItemStack result = new ItemStack(recipeOutput.getItem(), recipeOutput.getCount());

        var strainData = input.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (strainData != null) {
            result.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
            // Propagate strain ID for lineage tracking
            var strainId = input.get(ModDataComponentTypes.STRAIN_ID.get());
            if (strainId != null) result.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
            return result;
        }

        Integer thc = input.get(ModDataComponentTypes.THC.get());
        if (thc != null) result.set(ModDataComponentTypes.THC.get(), thc);

        Integer cbd = input.get(ModDataComponentTypes.CBD.get());
        if (cbd != null) result.set(ModDataComponentTypes.CBD.get(), cbd);

        // Preserve the base/active effect from the input item
        String eff = input.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        if (eff != null) result.set(ModDataComponentTypes.ACTIVE_INGREDIENT.get(), eff);

        return result;
    }

    private boolean areWeedDataEqual(ItemStack a, ItemStack b) {
        var aStrain = a.get(ModDataComponentTypes.STRAIN_DATA.get());
        var bStrain = b.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (!Objects.equals(aStrain, bStrain)) return false;

        Integer aTHC = a.get(ModDataComponentTypes.THC.get());
        Integer bTHC = b.get(ModDataComponentTypes.THC.get());
        if (!Objects.equals(aTHC, bTHC)) return false;

        Integer aCBD = a.get(ModDataComponentTypes.CBD.get());
        Integer bCBD = b.get(ModDataComponentTypes.CBD.get());
        if (!Objects.equals(aCBD, bCBD)) return false;

        // Ensure stacks only merge when the active effect matches
        String aEff = a.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        String bEff = b.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        return Objects.equals(aEff, bEff);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        // Check if has energy
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
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty() || level == null) return false;

        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack recipeOut = recipe.get().value().output();
        ItemStack candidate = buildOutputWithWeedData(input, recipeOut);

        return canInsertAmountIntoOutputSlot(candidate.getCount())
                && canInsertItemIntoOutputSlot(candidate);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack candidate) {
        ItemStack slot = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (slot.isEmpty()) return true;
        if (slot.getItem() != candidate.getItem()) return false;
        return areWeedDataEqual(slot, candidate);
    }



    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();
        return maxCount >= currentCount + count;
    }

    private Optional<RecipeHolder<ExtractorRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.EXTRACTOR_TYPE.get(), new ExtractorRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private void craftItem() {
        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty() || level == null) return;

        ItemStack recipeOut = recipe.get().value().output();
        ItemStack candidate = buildOutputWithWeedData(input, recipeOut);

        // Consume input
        itemHandler.extractItem(INPUT_SLOT, 1, false);

        // Merge into output slot (safe because hasRecipe() already validated compatibility)
        ItemStack outSlot = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outSlot.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, candidate);
        } else {
            // Grow count while preserving the existing data (identical to candidate)
            ItemStack merged = outSlot.copy();
            merged.grow(candidate.getCount());
            itemHandler.setStackInSlot(OUTPUT_SLOT, merged);
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
        tag.put("extractor.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("extractor.progress", progress);
        tag.putInt("extractor.maxProgress", maxProgress);
        tag.putInt("extractor.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("extractor.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("extractor.energy"));
        progress = tag.getInt("extractor.progress");
        maxProgress = tag.getInt("extractor.maxProgress");
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
