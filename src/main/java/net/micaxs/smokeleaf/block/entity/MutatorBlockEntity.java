package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.recipe.*;
import net.micaxs.smokeleaf.screen.custom.MutatorMenu;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainTankHolder;
import net.micaxs.smokeleaf.strain.StrainTankTracker;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
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

public class MutatorBlockEntity extends BlockEntity implements MenuProvider, StrainTankHolder {

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
    /** Stable ID for the current mixture batch — assigned once when the mixture is first loaded and reused
     *  for every seed produced from that batch so all seeds share the same strain lineage. */
    private String mixtureStrainId = null;


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
                case 0 -> stack.is(ModFluids.HASH_OIL_BUCKET) || stack.is(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET);
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
        // BUCKET_SLOT stays extractable too — it's a container-exchange slot (a filled bucket goes
        // in, gets auto-drained into the tank, and the empty bucket left behind needs to come back
        // out), not a raw-material input in the same sense as the seed/extract slots.
        return new ExtractRestrictedItemHandler(this.itemHandler, slot -> slot == OUTPUT_SLOT || slot == BUCKET_SLOT);
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
        if (output.is(ModItems.GENERIC_SEEDS.get())) {
            FluidStack mix = FLUID_TANK.getFluid();
            if (!mix.isEmpty() && mix.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
                // Prefer persisted mixtureStrain (FluidStack components may be lost in tank serialization).
                StrainData base = this.mixtureStrain != StrainData.EMPTY ? this.mixtureStrain : StrainUtil.getStrain(mix);

                // Legacy support: older mixtures may have only color/effects. If stats are missing, roll ONCE and persist.
                // Using mixtureStrainId as seed makes this deterministic — same strain always gets same stats.
                if (this.level != null) {
                    StrainData finalized = StrainUtil.finalizeMixtureStats(base, this.level.random, this.mixtureStrainId);
                    if (finalized != base && finalized != StrainData.EMPTY) {
                        base = finalized;
                        this.mixtureStrain = finalized;
                    }
                }

                // If still no strain, at least use fluid tint.
                if (base == StrainData.EMPTY) {
                    int color = IClientFluidTypeExtensions.of(mix.getFluid()).getTintColor(mix);
                    base = new StrainData(color, 0xFF4A7A2E, 0, 0, 0, 0, 0, java.util.List.of(), 0, 0, false, "", StrainData.TypeColors.NONE, "", "");
                }

                output.set(ModDataComponentTypes.STRAIN_DATA.get(), base);

                // Use the stable mixture strain ID so all seeds from this batch share lineage.
                // Prefer the fluid's own STRAIN_ID/MIX_KEY (the exact key the Mixer tagged its
                // output oil with) over a freshly content-derived hash — reusing the real mix key
                // is what lets a later identification (StrainRegistrySavedData#propagateUpdate ->
                // StrainTankTracker) find and patch any oil still sitting in the Mixer's own tank,
                // instead of silently mismatching against a differently-computed id and leaving
                // that leftover oil stuck showing "Unidentified" forever. Content hash stays as a
                // fallback for fluid that genuinely carries neither (legacy/edge cases).
                if (this.mixtureStrainId == null) {
                    String fromFluid = mix.get(ModDataComponentTypes.STRAIN_ID.get());
                    if (fromFluid == null || fromFluid.isBlank()) {
                        fromFluid = mix.get(ModDataComponentTypes.MIX_KEY.get());
                    }
                    this.mixtureStrainId = (fromFluid != null && !fromFluid.isBlank())
                            ? fromFluid
                            : StrainUtil.strainContentId(base);
                }
                String strainId = this.mixtureStrainId;
                output.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
                if (this.level instanceof net.minecraft.server.level.ServerLevel sl) {
                    StrainRegistrySavedData registry = StrainRegistrySavedData.get(sl.getServer());
                    String existingName = registry.lookupName(strainId);
                    // Keep any name already registered; only register fresh if unknown.
                    if (existingName == null || existingName.isBlank()) {
                        String displayName = base.identified() && !base.displayName().isBlank() ? base.displayName() : "";
                        registry.register(strainId, displayName, "", base.colorArgb(), base.leafColor(), base.thc(), base.cbd(),
                                base.nitrogen(), base.phosphorus(), base.potassium(),
                                base.baseStrain1(), base.baseStrain2());
                    } else if (!existingName.equals(base.displayName())) {
                        // Sync the name from the registry onto the output so it matches what was named earlier.
                    base = new StrainData(base.colorArgb(), base.leafColor(), base.thc(), base.cbd(),
                            base.nitrogen(), base.phosphorus(), base.potassium(),
                            base.effects(), base.amplifier(), base.durationTicks(),
                            true, existingName, base.typeColors(),
                            base.baseStrain1(), base.baseStrain2());
                    output.set(ModDataComponentTypes.STRAIN_DATA.get(), base);
                    }
                    // Set STRAIN_CREATOR from the registry so "Discovered by" tooltip shows on the seed.
                    StrainRegistrySavedData.StrainEntry entry = registry.lookup(strainId);
                    if (entry != null && !entry.creatorName().isBlank()) {
                        output.set(ModDataComponentTypes.STRAIN_CREATOR.get(), entry.creatorName());
                    }
                }
            }
        }

        // Remove inputs using exact counts from the recipe
        removeInputs(rec);

        // Drain fluid
        FLUID_TANK.drain(rec.getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // If we drained the mixture, clear persisted strain once tank empties.
        if (FLUID_TANK.isEmpty()) {
            mixtureStrain = StrainData.EMPTY;
            mixtureStrainId = null;
        }

        // Insert output
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int newCount = existing.getCount() + output.getCount();
        ItemStack newStack = new ItemStack(output.getItem(), newCount);

        // Prefer to preserve the existing strain payload when merging, otherwise use the new output's payload.
        if (existing.has(ModDataComponentTypes.STRAIN_DATA.get())) {
            StrainData e = existing.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (e != null) newStack.set(ModDataComponentTypes.STRAIN_DATA.get(), e);
        } else if (output.has(ModDataComponentTypes.STRAIN_DATA.get())) {
            StrainData o = output.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (o != null) newStack.set(ModDataComponentTypes.STRAIN_DATA.get(), o);
        }

        String existingId = existing.get(ModDataComponentTypes.STRAIN_ID.get());
        String outputId = output.get(ModDataComponentTypes.STRAIN_ID.get());
        if (existingId != null && !existingId.isBlank()) {
            newStack.set(ModDataComponentTypes.STRAIN_ID.get(), existingId);
        } else if (outputId != null && !outputId.isBlank()) {
            newStack.set(ModDataComponentTypes.STRAIN_ID.get(), outputId);
        }

        // Carry STRAIN_CREATOR (prefer existing slot's value, then new output's)
        String existingCreator = existing.get(ModDataComponentTypes.STRAIN_CREATOR.get());
        String outputCreator = output.get(ModDataComponentTypes.STRAIN_CREATOR.get());
        if (existingCreator != null && !existingCreator.isBlank()) {
            newStack.set(ModDataComponentTypes.STRAIN_CREATOR.get(), existingCreator);
        } else if (outputCreator != null && !outputCreator.isBlank()) {
            newStack.set(ModDataComponentTypes.STRAIN_CREATOR.get(), outputCreator);
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

        // Pre-apply the current mixture strain so the output slot compatibility check is accurate.
        // rec.output() is the bare recipe template (no STRAIN_DATA) but the machine will stamp strain
        // onto it during craftItem(). Without this, after the first seed is produced the output slot
        // has STRAIN_DATA and canInsertItemIntoOutputSlot would wrongly block further crafts.
        ItemStack output = rec.output().copy();
        if (output.is(ModItems.GENERIC_SEEDS.get()) && this.mixtureStrain != StrainData.EMPTY) {
            output.set(ModDataComponentTypes.STRAIN_DATA.get(), this.mixtureStrain);
            if (this.mixtureStrainId != null && !this.mixtureStrainId.isBlank()) {
                output.set(ModDataComponentTypes.STRAIN_ID.get(), this.mixtureStrainId);
            }
        }

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
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) return true;
        if (existing.getItem() != output.getItem()) return false;

        boolean existingHas = existing.has(ModDataComponentTypes.STRAIN_DATA.get());
        boolean outputHas = output.has(ModDataComponentTypes.STRAIN_DATA.get());

        // Neither has strain data → compatible plain items
        if (!existingHas && !outputHas) return true;

        // One has strain and the other doesn't → the output is the bare recipe template;
        // allow the insertion since craftItem() will stamp the same strain on it.
        if (existingHas && !outputHas) return true;
        if (!existingHas && outputHas) return true;

        // Both have strain data → compare by STRAIN_ID first (fastest), then by record equality,
        // then by deterministic content ID (handles cases where STRAIN_ID is absent).
        StrainData e = existing.get(ModDataComponentTypes.STRAIN_DATA.get());
        StrainData o = output.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (e == null) e = StrainData.EMPTY;
        if (o == null) o = StrainData.EMPTY;

        String eid = existing.get(ModDataComponentTypes.STRAIN_ID.get());
        String oid = output.get(ModDataComponentTypes.STRAIN_ID.get());
        if (eid == null) eid = "";
        if (oid == null) oid = "";

        if (!eid.isBlank() && !oid.isBlank()) return eid.equals(oid);
        if (e.equals(o)) return true;

        return StrainUtil.strainContentId(e).equals(StrainUtil.strainContentId(o));
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
                // Adopt or generate the stable strain ID for this mixture batch.
                String bucketStrainId = bucketStack.get(ModDataComponentTypes.STRAIN_ID.get());
                if (bucketStrainId == null || bucketStrainId.isBlank()) {
                    // Fallback: mixer-produced buckets carry MIX_KEY which equals STRAIN_ID.
                    bucketStrainId = bucketStack.get(ModDataComponentTypes.MIX_KEY.get());
                }
                if (bucketStrainId == null || bucketStrainId.isBlank()) {
                    // Last resort: derive a deterministic ID from strain content so the same stats
                    // always share the same lineage — even without an explicit STRAIN_ID.
                    bucketStrainId = StrainUtil.strainContentId(inStrain);
                }
                this.mixtureStrainId = bucketStrainId;

                // Immediately sync the registered name (if any) so seeds come out named.
                if (this.level instanceof net.minecraft.server.level.ServerLevel sl) {
                    String registeredName = StrainRegistrySavedData.get(sl.getServer()).lookupName(this.mixtureStrainId);
                    if (registeredName != null && !registeredName.isBlank()
                            && !registeredName.equals(this.mixtureStrain.displayName())) {
                        this.mixtureStrain = new StrainData(
                                this.mixtureStrain.colorArgb(), this.mixtureStrain.leafColor(),
                                this.mixtureStrain.thc(), this.mixtureStrain.cbd(),
                                this.mixtureStrain.nitrogen(), this.mixtureStrain.phosphorus(), this.mixtureStrain.potassium(),
                                this.mixtureStrain.effects(), this.mixtureStrain.amplifier(), this.mixtureStrain.durationTicks(),
                                true, registeredName, this.mixtureStrain.typeColors(),
                                this.mixtureStrain.baseStrain1(), this.mixtureStrain.baseStrain2()
                        );
                    }
                }
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
        if (mixtureStrainId != null) {
            tag.putString("mutator.mixture_strain_id", mixtureStrainId);
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
        mixtureStrainId = null;
        if (tag.contains("mutator.mixture_strain")) {
            StrainData.CODEC.parse(NbtOps.INSTANCE, tag.get("mutator.mixture_strain"))
                    .result()
                    .ifPresent(d -> mixtureStrain = d);
        }
        if (tag.contains("mutator.mixture_strain_id")) {
            mixtureStrainId = tag.getString("mutator.mixture_strain_id");
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

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) StrainTankTracker.register(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        StrainTankTracker.unregister(this);
    }

    @Override
    public void applyStrainRegistryUpdate(String strainId, StrainRegistrySavedData.StrainEntry entry) {
        if (strainId.equals(this.mixtureStrainId) && this.mixtureStrain != StrainData.EMPTY) {
            this.mixtureStrain = StrainRegistrySavedData.withEntryApplied(this.mixtureStrain, entry);
            setChanged();
        }

        // Patch any seed already sitting in this machine's own slots (most commonly the output
        // slot) that shares this strainId — otherwise a batch-crafted seed left uncollected here
        // never picks up a rename made afterward via the Strain Identifier, since renames are only
        // ever pushed to items already in an online player's inventory.
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            StrainRegistrySavedData.applyUpdate(itemHandler.getStackInSlot(slot), strainId, entry);
        }
        setChanged();

        FluidStack fluid = FLUID_TANK.getFluid();
        if (fluid.isEmpty()) return;
        String fluidStrainId = fluid.get(ModDataComponentTypes.STRAIN_ID.get());
        if (!strainId.equals(fluidStrainId)) return;
        StrainData current = StrainUtil.getStrain(fluid);
        if (current == StrainData.EMPTY) return;

        FluidStack updated = fluid.copy();
        StrainUtil.setStrain(updated, StrainRegistrySavedData.withEntryApplied(current, entry));
        if (!entry.creatorName().isBlank()) updated.set(ModDataComponentTypes.STRAIN_CREATOR.get(), entry.creatorName());
        FLUID_TANK.setFluid(updated);
        // FluidTank#setFluid just overwrites the field directly (unlike fill()/drain(), it never
        // calls onContentsChanged()), so without an explicit sync here the server-side data is
        // correct but no update packet goes out, leaving an already-open GUI/tooltip stale until
        // something else (relog, chunk reload) forces a fresh sync.
        if (level != null && !level.isClientSide()) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
}
