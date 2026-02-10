package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.recipe.*;
import net.micaxs.smokeleaf.screen.custom.MutatorMenu;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

    /**
     * Strain payload for the mixture currently in the tank.
     * We persist this separately because FluidTank serialization may drop custom FluidStack components.
     */
    private StrainData mixtureStrain = StrainData.EMPTY;



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
                case 0 -> stack.is(ModFluids.HASH_OIL_BUCKET) || stack.is(ModFluids.HEMP_OIL_BUCKET) || stack.is(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET);
                case 1, 2 -> {
                    if (stack.isEmpty() || level == null) yield false;
                    yield level.getRecipeManager()
                            .getAllRecipesFor(ModRecipes.MUTATOR_TYPE.get())
                            .stream()
                            .anyMatch(holder -> holder.value().inputItems().stream().anyMatch(iwc -> iwc.ingredient().test(stack)));
                }
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
        // What the GUI should render.
        FluidStack tank = FLUID_TANK.getFluid();

        // If the tank contains the player-made mixture, ensure the rendered stack carries strain data.
        if (!tank.isEmpty() && tank.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
            StrainData strain = StrainUtil.getStrain(tank);
            if (strain == StrainData.EMPTY && this.mixtureStrain != StrainData.EMPTY) {
                FluidStack copy = tank.copy();
                copy.set(ModDataComponentTypes.STRAIN_DATA.get(), this.mixtureStrain);
                return copy;
            }
        }

        // Legacy fallback: if tank is missing STRAIN_DATA, try the currently inserted bucket.
        if (!tank.isEmpty()
                && tank.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()
                && !tank.has(ModDataComponentTypes.STRAIN_DATA.get())) {
            ItemStack bucketStack = itemHandler.getStackInSlot(BUCKET_SLOT);
            FluidStack fromBucket = FluidUtil.getFluidContained(bucketStack).orElse(FluidStack.EMPTY);
            if (!fromBucket.isEmpty() && fromBucket.has(ModDataComponentTypes.STRAIN_DATA.get())) {
                FluidStack copy = tank.copy();
                copy.set(ModDataComponentTypes.STRAIN_DATA.get(), fromBucket.get(ModDataComponentTypes.STRAIN_DATA.get()));
                return copy;
            }
        }

        return tank;
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
                    case 0:
                        MutatorBlockEntity.this.progress = value;
                        break;
                    case 1:
                        MutatorBlockEntity.this.maxProgress = value;
                        break;
                    default:
                        break;
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

        // If this is the custom strain recipe, copy strain data from the mixture fluid.
        if (output.is(ModItems.UNIDENTIFIED_SEEDS.get())) {
            FluidStack mix = FLUID_TANK.getFluid();
            if (!mix.isEmpty() && mix.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
                // Prefer persisted mixtureStrain (FluidStack components may be lost in tank serialization).
                StrainData base = this.mixtureStrain != StrainData.EMPTY ? this.mixtureStrain : StrainUtil.getStrain(mix);

                // Legacy support: older mixtures may have only color/effects. If stats are missing, roll ONCE and persist.
                if (this.level != null) {
                    StrainData finalized = StrainUtil.finalizeMixtureStats(base, this.level.random);
                    if (finalized != base && finalized != StrainData.EMPTY) {
                        base = finalized;
                        this.mixtureStrain = finalized;
                    }
                }

                // If still no strain, at least use fluid tint.
                if (base == StrainData.EMPTY) {
                    int color = IClientFluidTypeExtensions.of(mix.getFluid()).getTintColor(mix);
                    base = new StrainData(color, 0, 0, 0, 0, 0, java.util.List.of(), 0, 0, false, "");
                }

                output.set(ModDataComponentTypes.STRAIN_DATA.get(), base);
            }
        }

        // Remove inputs using exact counts from the recipe
        removeInputs(rec);

        // Drain fluid
        FLUID_TANK.drain(rec.getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // If we drained the mixture, clear persisted strain once tank empties.
        if (FLUID_TANK.isEmpty()) {
            mixtureStrain = StrainData.EMPTY;
        }

        // Insert output
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int newCount = existing.getCount() + output.getCount();
        ItemStack newStack = new ItemStack(output.getItem(), newCount);
        if (output.has(ModDataComponentTypes.STRAIN_DATA.get())) {
            newStack.set(ModDataComponentTypes.STRAIN_DATA.get(), output.get(ModDataComponentTypes.STRAIN_DATA.get()));
        }
        itemHandler.setStackInSlot(OUTPUT_SLOT, newStack);
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
        if (this.level == null) return Optional.empty();

        ItemStack seedStack = itemHandler.getStackInSlot(SEED_INPUT_SLOT);
        ItemStack extractStack = itemHandler.getStackInSlot(EXTRACT_INPUT_SLOT);

        if (seedStack.isEmpty() || extractStack.isEmpty()) return Optional.empty();

        return this.level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.MUTATOR_TYPE.get())
                .stream()
                .filter(holder -> {
                    MutatorRecipe rec = holder.value();
                    NonNullList<IngredientWithCount> inputs = rec.inputItems();
                    if (inputs.isEmpty()) return false;
                    boolean seedOk = matches(inputs, 0, seedStack);
                    boolean extractOk = inputs.size() < 2 || matches(inputs, 1, extractStack);
                    FluidStack required = rec.getFluid();
                    FluidStack inTank = FLUID_TANK.getFluid();
                    boolean fluidOk = !inTank.isEmpty()
                            && inTank.getFluid() == required.getFluid()
                            && inTank.getAmount() >= required.getAmount();
                    return seedOk && extractOk && fluidOk;
                })
                .findFirst();
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

        // Special handling for the player-made mixture: persist the strain separately + keep tank in sync.
        if (fluidInBucket.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
            // The mixture bucket stores strain on the ItemStack itself. The FluidStack returned by FluidUtil
            // may not carry components, so read from the bucket item first.
            StrainData inStrain = StrainUtil.getStrain(bucketStack);
            if (inStrain == StrainData.EMPTY) {
                inStrain = StrainUtil.getStrain(fluidInBucket);
            }

            // If the tank already contains mixture strain, only allow stacking if it matches.
            if (this.mixtureStrain != StrainData.EMPTY && inStrain != StrainData.EMPTY && !this.mixtureStrain.equals(inStrain)) {
                return; // different mixture, don't merge
            }

            // Adopt strain if we don't have one yet.
            if (this.mixtureStrain == StrainData.EMPTY && inStrain != StrainData.EMPTY) {
                this.mixtureStrain = inStrain;
            }

            // Build the stack we try to insert, ensuring STRAIN_DATA is present so NeoForge treats it as the same stack.
            FluidStack toInsert = fluidInBucket.copy();
            if (this.mixtureStrain != StrainData.EMPTY) {
                toInsert.set(ModDataComponentTypes.STRAIN_DATA.get(), this.mixtureStrain);
            }

            int actuallyFilled = FLUID_TANK.fill(toInsert, IFluidHandler.FluidAction.EXECUTE);
            if (actuallyFilled <= 0) {
                return;
            }

            // Ensure the tank fluid stays tagged with the strain data (some paths may drop components).
            FluidStack inTankNow = FLUID_TANK.getFluid();
            if (!inTankNow.isEmpty() && this.mixtureStrain != StrainData.EMPTY && !inTankNow.has(ModDataComponentTypes.STRAIN_DATA.get())) {
                FluidStack copy = inTankNow.copy();
                copy.set(ModDataComponentTypes.STRAIN_DATA.get(), this.mixtureStrain);
                FLUID_TANK.setFluid(copy);
            }

            // Consume the bucket as normal.
            ItemStack emptyBucket = new ItemStack(bucketStack.getItem().getCraftingRemainingItem());
            itemHandler.setStackInSlot(BUCKET_SLOT, emptyBucket);

            // Force-save/sync strain immediately (it's not part of the tank NBT in all NeoForge paths).
            setChanged();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            return;
        }

        int filledAmount = FLUID_TANK.fill(fluidInBucket, IFluidHandler.FluidAction.SIMULATE);
        if (filledAmount <= 0) {
            return;
        }

        // Default path for all other fluids.
        FluidStack toInsert = fluidInBucket.copyWithAmount(filledAmount);
        int actuallyFilled = FLUID_TANK.fill(toInsert, IFluidHandler.FluidAction.EXECUTE);
        if (actuallyFilled > 0) {
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

    private boolean matches(NonNullList<IngredientWithCount> inputs, int index, ItemStack stack) {
        if (index >= inputs.size()) return false;
        IngredientWithCount need = inputs.get(index);
        int required = Math.max(1, need.count());
        return need.ingredient().test(stack) && stack.getCount() >= required;
    }

    // NBT Data
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("mutator.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("mutator.progress", progress);
        tag.putInt("mutator.maxProgress", maxProgress);
        tag.putInt("mutator.energy", ENERGY_STORAGE.getEnergyStored());

        tag.put("mutator.tank", FLUID_TANK.writeToNBT(registries, new CompoundTag()));

        // Persist mixture strain separately (FluidTank may drop custom FluidStack components).
        if (mixtureStrain != StrainData.EMPTY) {
            StrainData.CODEC.encodeStart(NbtOps.INSTANCE, mixtureStrain)
                    .result()
                    .ifPresent(t -> tag.put("mutator.mixture_strain", t));
        }

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("mutator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("mutator.energy"));
        progress = tag.getInt("mutator.progress");
        maxProgress = tag.getInt("mutator.maxProgress");

        if (tag.contains("mutator.tank")) {
            FLUID_TANK.readFromNBT(registries, tag.getCompound("mutator.tank"));
        }

        mixtureStrain = StrainData.EMPTY;
        if (tag.contains("mutator.mixture_strain")) {
            StrainData.CODEC.parse(NbtOps.INSTANCE, tag.get("mutator.mixture_strain"))
                    .result()
                    .ifPresent(d -> mixtureStrain = d);
        }
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
