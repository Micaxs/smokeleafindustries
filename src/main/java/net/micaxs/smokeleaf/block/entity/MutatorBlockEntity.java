package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.recipe.*;
import net.micaxs.smokeleaf.screen.custom.MutatorMenu;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MutatorBlockEntity extends BlockEntity implements MenuProvider {

    private static final int ENERGY_CONSTANT = 40;
    private static final int BUCKET_SLOT = 0;
    private static final int SEED_INPUT_SLOT = 1;
    private static final int EXTRACT_INPUT_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 82;



    // Inventory Capability
    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
              level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch(slot) {
                case 0 -> stack.is(ModFluids.HASH_OIL_BUCKET);
                case 1 -> stack.is(ModTags.WEED_SEEDS);
                case 2 -> stack.is(ModTags.WEED_EXTRACTS);
                case 3 -> false;
                default -> false;
            };
        }
    };
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return this.itemHandler;
    }



    // Energy Capability
    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
            }
        };
    }
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }



    // Fluid Capability
    private final FluidTank FLUID_TANK = createFluidTank();
    private FluidTank createFluidTank() {
        return new FluidTank(8000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                super.onContentsChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return true;
            }
        };
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }
    public IFluidHandler getTank(@Nullable Direction direction) {
        return FLUID_TANK;
    }



    public MutatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MUTATOR_BE.get(), pos, blockState);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> MutatorBlockEntity.this.progress;
                    case 1 -> MutatorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: MutatorBlockEntity.this.progress = value;
                    case 1: MutatorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Seed Mutator");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MutatorMenu(i, inventory, this, this.data);
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
        boolean hasFluid = !this.FLUID_TANK.isEmpty();

        // Handle insertion of Hash oil Bucket into Fluid Tank
        if (hasFluidItemInSourceSlot()) {
            transferItemFluidToFluidTank();
        }
        
        // Crafting
        if (hasEnergy && hasFluid && hasRecipe()) {
            increaseCraftingProgress();
            this.ENERGY_STORAGE.extractEnergy(20, false);
            
            // Smoke Particles
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


    private void craftItem() {
        Optional<RecipeHolder<MutatorRecipe>> opt = getCurrentRecipe();
        if (opt.isEmpty()) return;

        MutatorRecipe rec = opt.get().value();
        ItemStack output = rec.output().copy();

        // Remove inputs using exact counts from the recipe
        removeInputs(rec);

        // Drain fluid
        FLUID_TANK.drain(rec.getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // Insert output
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int newCount = existing.getCount() + output.getCount();
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(), newCount));
    }

    private void removeInputs(MutatorRecipe rec) {
        NonNullList<IngredientWithCount> inputs = rec.inputItems();
        if (!inputs.isEmpty()) {
            int c0 = Math.max(1, inputs.get(0).count());
            this.itemHandler.extractItem(SEED_INPUT_SLOT, c0, false);
        }
        if (inputs.size() > 1) {
            int c1 = Math.max(1, inputs.get(1).count());
            this.itemHandler.extractItem(EXTRACT_INPUT_SLOT, c1, false);
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<MutatorRecipe>> opt = getCurrentRecipe();
        if (opt.isEmpty()) return false;

        MutatorRecipe rec = opt.get().value();
        ItemStack output = rec.output();
        if (!canInsertAmountIntoOutputSlot(output.getCount()) || !canInsertItemIntoOutputSlot(output)) return false;

        FluidStack tank = FLUID_TANK.getFluid();
        FluidStack required = rec.getFluid();
        if (tank.isEmpty()) return false;

        // Check Fluids
        if (tank.getFluid() != required.getFluid()) return false;
        if (tank.getAmount() < required.getAmount()) return false;

        return true;
    }

    private Optional<RecipeHolder<MutatorRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.MUTATOR_TYPE.get(), new MutatorRecipeInput(itemHandler.getStackInSlot(SEED_INPUT_SLOT), itemHandler.getStackInSlot(EXTRACT_INPUT_SLOT)), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();
        return maxCount >= currentCount + count;
    }

    private void transferItemFluidToFluidTank() {
        ItemStack bucketStack = itemHandler.getStackInSlot(BUCKET_SLOT);
        if (bucketStack.isEmpty()) {
            return;
        }

        FluidStack fluidInBucket = FluidUtil.getFluidContained(bucketStack).orElse(FluidStack.EMPTY);
        if (fluidInBucket.isEmpty()) {
            return;
        }

        int filledAmount = FLUID_TANK.fill(fluidInBucket, IFluidHandler.FluidAction.SIMULATE);
        if (filledAmount > 0) {
            FLUID_TANK.fill(fluidInBucket, IFluidHandler.FluidAction.EXECUTE);
            ItemStack emptyBucket = new ItemStack(bucketStack.getItem().getCraftingRemainingItem());
            itemHandler.setStackInSlot(BUCKET_SLOT, emptyBucket);
        }
    }
    
    private boolean hasFluidItemInSourceSlot() {
        ItemStack stack = itemHandler.getStackInSlot(BUCKET_SLOT);
        return !stack.isEmpty() && FluidUtil.getFluidContained(stack).isPresent();
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
        tag.put("mutator.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("mutator.progress", progress);
        tag.putInt("mutator.maxProgress", maxProgress);
        tag.putInt("mutator.energy", ENERGY_STORAGE.getEnergyStored());
        tag = FLUID_TANK.writeToNBT(registries, tag);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("mutator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("mutator.energy"));
        progress = tag.getInt("mutator.progress");
        maxProgress = tag.getInt("mutator.maxProgress");
        FLUID_TANK.readFromNBT(registries, tag);
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
