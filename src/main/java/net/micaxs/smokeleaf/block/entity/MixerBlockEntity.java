package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.fluid.WeedFluidStackUtil;
import net.micaxs.smokeleaf.strain.MixedStrainSavedData;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainTankHolder;
import net.micaxs.smokeleaf.strain.StrainTankTracker;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.screen.custom.MixerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MixerBlockEntity extends BlockEntity implements MenuProvider, StrainTankHolder {

    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            if (getLevel() != null) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
        }
    };

    // Slot 0/1: bucket/container inputs for A/B tanks
    private static final int SLOT_A = 0;
    private static final int SLOT_B = 1;

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Any fluid container is allowed; we validate fluid types when transferring.
            return slot == SLOT_A || slot == SLOT_B;
        }
    };

    public IItemHandler getItemHandler(@Nullable Direction dir) {
        return itemHandler;
    }

    private FluidTank createInputTank() {
        return new FluidTank(4000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                super.onContentsChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return !stack.isEmpty() && ModFluids.isExtractFluid(stack.getFluid());
            }
        };
    }

    private FluidTank createOutputTank() {
        return new FluidTank(8000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                super.onContentsChanged();
            }
        };
    }

    private final FluidTank TANK_A = createInputTank();
    private final FluidTank TANK_B = createInputTank();
    private final FluidTank TANK_OUT = createOutputTank();

    /**
     * GUI helper: allow draining from input tanks as well (player can remove mistakes).
     * The *capability* still only drains from output (see EXPOSED_FLUID_HANDLER).
     */
    private final IFluidHandler TANK_A_IO = TANK_A;
    private final IFluidHandler TANK_B_IO = TANK_B;

    /** Output tank should never accept inserts via pipes/GUI. */
    private final IFluidHandler TANK_OUT_DRAIN_ONLY = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return TANK_OUT.getFluid(); }

        @Override
        public int getTankCapacity(int tank) { return TANK_OUT.getCapacity(); }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            // No filling via this handler.
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return TANK_OUT.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return TANK_OUT.drain(maxDrain, action);
        }
    };

    public IFluidHandler getTankA(@Nullable Direction dir) { return TANK_A_IO; }
    public IFluidHandler getTankB(@Nullable Direction dir) { return TANK_B_IO; }
    public IFluidHandler getTankOut(@Nullable Direction dir) { return TANK_OUT_DRAIN_ONLY; }

    public FluidStack getFluidA() { return TANK_A.getFluid(); }
    public FluidStack getFluidB() { return TANK_B.getFluid(); }
    public FluidStack getFluidOut() { return TANK_OUT.getFluid(); }

    /**
     * Exposed fluid capability (all sides):
     * - tanks 0-1: inputs (extract fluids only, insert only)
     * - tank 2: output (unidentified mixture, extract only)
     */
    private final IFluidHandler EXPOSED_FLUID_HANDLER = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 3;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return switch (tank) {
                case 0 -> TANK_A.getFluid();
                case 1 -> TANK_B.getFluid();
                case 2 -> TANK_OUT.getFluid();
                default -> FluidStack.EMPTY;
            };
        }

        @Override
        public int getTankCapacity(int tank) {
            return switch (tank) {
                case 0 -> TANK_A.getCapacity();
                case 1 -> TANK_B.getCapacity();
                case 2 -> TANK_OUT.getCapacity();
                default -> 0;
            };
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return switch (tank) {
                case 0 -> TANK_A.isFluidValid(stack);
                case 1 -> TANK_B.isFluidValid(stack);
                case 2 -> false; // output is never fillable via capability
                default -> false;
            };
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            // Only allow insertion into input tanks A/B. Output is extract-only.
            if (resource.isEmpty()) return 0;

            // Keep tanks separate: fill at most ONE tank per call.
            // Prefer filling a tank that already contains this fluid; otherwise the first empty one.
            if (!TANK_A.isEmpty() && TANK_A.getFluid().is(resource.getFluid())) {
                return TANK_A.fill(resource, action);
            }
            if (!TANK_B.isEmpty() && TANK_B.getFluid().is(resource.getFluid())) {
                return TANK_B.fill(resource, action);
            }

            if (TANK_A.isEmpty()) {
                return TANK_A.fill(resource, action);
            }
            if (TANK_B.isEmpty()) {
                return TANK_B.fill(resource, action);
            }

            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            // Only allow extraction from output tank.
            return TANK_OUT.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            // Only allow extraction from output tank.
            return TANK_OUT.drain(maxDrain, action);
        }
    };

    public IFluidHandler getFluidHandler(@Nullable Direction dir) {
        return EXPOSED_FLUID_HANDLER;
    }

    private int progress = 0;
    private int maxProgress = 100;

    protected final ContainerData data;

    public MixerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIXER_BE.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                if (i == 0) progress = value;
                if (i == 1) maxProgress = value;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Weed Mixer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new MixerMenu(id, inv, this, data);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction dir) { return ENERGY_STORAGE; }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) inv.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        // Move fluid from containers into the input tanks.
        if (!itemHandler.getStackInSlot(SLOT_A).isEmpty()) {
            FluidActionResult res = FluidUtil.tryEmptyContainer(itemHandler.getStackInSlot(SLOT_A), TANK_A, 1000, null, true);
            if (res.isSuccess()) {
                itemHandler.setStackInSlot(SLOT_A, res.getResult());
            }
        }
        if (!itemHandler.getStackInSlot(SLOT_B).isEmpty()) {
            FluidActionResult res = FluidUtil.tryEmptyContainer(itemHandler.getStackInSlot(SLOT_B), TANK_B, 1000, null, true);
            if (res.isSuccess()) {
                itemHandler.setStackInSlot(SLOT_B, res.getResult());
            }
        }

        boolean hasEnergy = ENERGY_STORAGE.getEnergyStored() > 0;
        boolean canMix = canMix();

        if (hasEnergy && canMix) {
            progress++;
            ENERGY_STORAGE.extractEnergy(20, false);

            if (level.random.nextInt(2) == 0) {
                level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, 0, 0);
            }

            if (progress >= maxProgress) {
                doMix();
                progress = 0;
            }
            setChanged(level, pos, state);
        } else {
            if (progress != 0) {
                progress = 0;
                setChanged(level, pos, state);
            }
        }

        boolean shouldBePowered = (progress > 0) || (hasEnergy && canMix);
        if (state.getValue(BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    /** Returns true for any fluid that is valid as a mixer input (Hash Oil, or the generic mixture fluid). */
    private static boolean isOilFluid(net.minecraft.world.level.material.Fluid fluid) {
        return ModFluids.isExtractFluid(fluid);
    }

    private boolean canMix() {
        if (TANK_A.isEmpty() || TANK_B.isEmpty()) return false;

        // Inputs must be extract or named mixture fluids.
        FluidStack a = TANK_A.getFluid();
        FluidStack b = TANK_B.getFluid();
        if (!isOilFluid(a.getFluid()) || !isOilFluid(b.getFluid())) return false;

        int drainEach = 250;
        if (a.getAmount() < drainEach || b.getAmount() < drainEach) return false;

        StrainData preview;
        if (isSameStrain(a, b)) {
            // Pouring one strain into both tanks isn't a blend — there's nothing to mix, so the
            // "output" is just that same strain, unchanged. Skip the effect-count-cap check below
            // too: it exists to stop a blend's *union* of two different effect pools from exceeding
            // the per-strain max, which is meaningless when both inputs already are one strain.
            preview = StrainUtil.getStrain(a) != StrainData.EMPTY ? StrainUtil.getStrain(a) : StrainUtil.getStrain(b);
        } else {
            // Block mix if the combined effect count would exceed the per-strain maximum.
            int aTint = IClientFluidTypeExtensions.of(a.getFluid()).getTintColor(a);
            int bTint = IClientFluidTypeExtensions.of(b.getFluid()).getTintColor(b);
            preview = StrainUtil.mixFromExtracts(a, aTint, b, bTint);
            // mixFromExtracts already caps at MAX_EFFECTS; but if input oils each have MAX_EFFECTS and
            // they are different, the total union would exceed the cap before truncation. We enforce it here.
            var combinedEffects = new java.util.LinkedHashSet<net.minecraft.resources.ResourceLocation>();
            net.micaxs.smokeleaf.fluid.WeedFluidData wa = WeedFluidStackUtil.getWeedData(a);
            net.micaxs.smokeleaf.fluid.WeedFluidData wb = WeedFluidStackUtil.getWeedData(b);
            if (wa != null) combinedEffects.addAll(wa.effects());
            if (wb != null) combinedEffects.addAll(wb.effects());
            if (StrainUtil.hasStrain(a)) combinedEffects.addAll(StrainUtil.getStrain(a).effects());
            if (StrainUtil.hasStrain(b)) combinedEffects.addAll(StrainUtil.getStrain(b).effects());
            if (combinedEffects.size() > StrainUtil.MAX_EFFECTS) return false;
        }

        // Output space
        FluidStack out = TANK_OUT.getFluid();
        if (!out.isEmpty() && out.getFluid() != ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) return false;

        // If output already has mixture in it, only allow stacking if the content fields match
        // (ignoring identified/displayName so a newly-named batch can stack with the first batch).
        if (!out.isEmpty() && out.getFluid() == ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
            StrainData existing = StrainUtil.getStrain(out);
            // If the tank lost components somehow, treat it as non-stackable to avoid overwriting.
            if (existing == StrainData.EMPTY) return false;
            if (!strainContentMatches(existing, preview)) return false;
        }

        return TANK_OUT.getFluidAmount() + 500 <= TANK_OUT.getCapacity();
    }

    /** True when both fluids carry the same non-blank STRAIN_ID — pouring a strain's oil into both tanks isn't a real blend. */
    private static boolean isSameStrain(FluidStack a, FluidStack b) {
        String idA = a.get(ModDataComponentTypes.STRAIN_ID.get());
        String idB = b.get(ModDataComponentTypes.STRAIN_ID.get());
        return idA != null && !idA.isBlank() && idA.equals(idB);
    }

    private void doMix() {
        int drainEach = 250;

        FluidStack a = TANK_A.drain(drainEach, IFluidHandler.FluidAction.EXECUTE);
        FluidStack b = TANK_B.drain(drainEach, IFluidHandler.FluidAction.EXECUTE);
        if (a.isEmpty() || b.isEmpty()) return;

        if (isSameStrain(a, b)) {
            // Same strain in both tanks — pass it straight through as-is (still identified, still
            // that exact strain) instead of generating a brand-new "unidentified" hybrid that would
            // need re-identifying from scratch for no reason.
            String strainId = a.get(ModDataComponentTypes.STRAIN_ID.get());
            StrainData same = StrainUtil.getStrain(a) != StrainData.EMPTY ? StrainUtil.getStrain(a) : StrainUtil.getStrain(b);
            FluidStack passthrough = new FluidStack(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), 500);
            StrainUtil.setStrain(passthrough, same);
            passthrough.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
            String creator = a.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (creator == null || creator.isBlank()) creator = b.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (creator != null && !creator.isBlank()) passthrough.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creator);
            if (!same.effects().isEmpty()) {
                WeedFluidStackUtil.withWeedData(passthrough, same.effects(), same.amplifier(), same.durationTicks());
            }
            TANK_OUT.fill(passthrough, IFluidHandler.FluidAction.EXECUTE);
            return;
        }

        int aTint = net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(a.getFluid()).getTintColor(a);
        int bTint = net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(b.getFluid()).getTintColor(b);

        StrainData mixed = StrainUtil.mixFromExtracts(a, aTint, b, bTint);

        // Compute canonical mix key.
        // Priority: display name > STRAIN_ID > fluid registry ID (stable, unique fallback for
        // old-style extract fluids that carry no STRAIN_DATA or STRAIN_ID).
        String nameA = StrainUtil.hasStrain(a) ? StrainUtil.getStrain(a).displayName() : "";
        String nameB = StrainUtil.hasStrain(b) ? StrainUtil.getStrain(b).displayName() : "";
        if (nameA == null) nameA = "";
        if (nameB == null) nameB = "";
        String strainIdA = a.get(ModDataComponentTypes.STRAIN_ID.get());
        String strainIdB = b.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainIdA == null) strainIdA = "";
        if (strainIdB == null) strainIdB = "";

        // If display names are empty, attempt registry lookup by STRAIN_ID to get the name
        if (level instanceof ServerLevel sl) {
            if (nameA.isBlank() && !strainIdA.isBlank()) {
                String looked = StrainRegistrySavedData.get(sl.getServer()).lookupName(strainIdA);
                if (looked != null && !looked.isBlank()) nameA = looked;
            }
            if (nameB.isBlank() && !strainIdB.isBlank()) {
                String looked = StrainRegistrySavedData.get(sl.getServer()).lookupName(strainIdB);
                if (looked != null && !looked.isBlank()) nameB = looked;
            }
        }

        // Fall back to the fluid's Minecraft registry ID — every registered fluid type is unique,
        // so this produces a stable, unique key even for old-style extract fluids without STRAIN_DATA.
        String fluidKeyA = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(a.getFluid()).toString();
        String fluidKeyB = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(b.getFluid()).toString();
        String keyA = !nameA.isBlank() ? nameA : (!strainIdA.isBlank() ? strainIdA : fluidKeyA);
        String keyB = !nameB.isBlank() ? nameB : (!strainIdB.isBlank() ? strainIdB : fluidKeyB);
        String mixKey = MixedStrainSavedData.canonicalKey(keyA, keyB);

        // Look up existing name for this combination (server-wide persistent registry)
        String resolvedCreator = "";
        if (level instanceof ServerLevel sl) {
            // Check MixedStrainSavedData first (mixer-blend specific registry)
            String registeredName = MixedStrainSavedData.get(sl.getServer()).lookup(mixKey);
            // Also check StrainRegistrySavedData by mixKey (covers renamed seeds/buckets)
            if (registeredName == null || registeredName.isBlank()) {
                registeredName = StrainRegistrySavedData.get(sl.getServer()).lookupName(mixKey);
            }
            if (registeredName != null && !registeredName.isBlank()) {
                mixed = new StrainData(
                        mixed.colorArgb(), mixed.leafColor(), mixed.thc(), mixed.cbd(),
                        mixed.nitrogen(), mixed.phosphorus(), mixed.potassium(),
                        mixed.effects(), mixed.amplifier(), mixed.durationTicks(),
                        true, registeredName, mixed.typeColors(),
                        mixed.baseStrain1(), mixed.baseStrain2()
                );
                // Carry the discoverer name so the bucket item can show "Discovered by"
                net.micaxs.smokeleaf.strain.StrainRegistrySavedData.StrainEntry entry =
                        StrainRegistrySavedData.get(sl.getServer()).lookup(mixKey);
                if (entry != null && !entry.creatorName().isBlank()) {
                    resolvedCreator = entry.creatorName();
                }
            }
        }

        FluidStack out = new FluidStack(ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get(), 500);
        StrainUtil.setStrain(out, mixed);
        out.set(ModDataComponentTypes.MIX_KEY.get(), mixKey);
        out.set(ModDataComponentTypes.STRAIN_ID.get(), mixKey);
        if (!resolvedCreator.isBlank()) {
            out.set(ModDataComponentTypes.STRAIN_CREATOR.get(), resolvedCreator);
        }

        // Also carry effect payload for consumption.
        if (!mixed.effects().isEmpty()) {
            WeedFluidStackUtil.withWeedData(out, mixed.effects(), mixed.amplifier(), mixed.durationTicks());
        }

        TANK_OUT.fill(out, IFluidHandler.FluidAction.EXECUTE);
    }

    /** Compare strain data ignoring identity fields (identified, displayName, typeColors). */
    private static boolean strainContentMatches(StrainData a, StrainData b) {
        return a.colorArgb() == b.colorArgb()
                && a.leafColor() == b.leafColor()
                && a.thc() == b.thc()
                && a.cbd() == b.cbd()
                && a.nitrogen() == b.nitrogen()
                && a.phosphorus() == b.phosphorus()
                && a.potassium() == b.potassium()
                && a.effects().equals(b.effects())
                && a.amplifier() == b.amplifier()
                && a.durationTicks() == b.durationTicks();
    }

    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        tag.put("mixer.inventory", itemHandler.serializeNBT(regs));
        tag.putInt("mixer.progress", progress);
        tag.putInt("mixer.maxProgress", maxProgress);
        tag.putInt("mixer.energy", ENERGY_STORAGE.getEnergyStored());

        tag.put("mixer.tank_a", TANK_A.writeToNBT(regs, new CompoundTag()));
        tag.put("mixer.tank_b", TANK_B.writeToNBT(regs, new CompoundTag()));
        tag.put("mixer.tank_out", TANK_OUT.writeToNBT(regs, new CompoundTag()));

        super.saveAdditional(tag, regs);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        itemHandler.deserializeNBT(regs, tag.getCompound("mixer.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("mixer.energy"));
        progress = tag.getInt("mixer.progress");
        maxProgress = tag.getInt("mixer.maxProgress");

        if (tag.contains("mixer.tank_a")) TANK_A.readFromNBT(regs, tag.getCompound("mixer.tank_a"));
        if (tag.contains("mixer.tank_b")) TANK_B.readFromNBT(regs, tag.getCompound("mixer.tank_b"));
        if (tag.contains("mixer.tank_out")) TANK_OUT.readFromNBT(regs, tag.getCompound("mixer.tank_out"));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider regs) {
        return saveWithoutMetadata(regs);
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
        boolean changed = patchTank(TANK_A, strainId, entry);
        changed |= patchTank(TANK_B, strainId, entry);
        changed |= patchTank(TANK_OUT, strainId, entry);
        // FluidTank#setFluid (used by patchTank below) just overwrites the field directly — unlike
        // fill()/drain(), it never calls onContentsChanged(), so without this the server-side data
        // is correct (which is why a relog picks it up) but no update packet ever goes out, leaving
        // the client's already-open GUI/tooltip showing the stale pre-identification fluid.
        if (changed && level != null && !level.isClientSide()) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private boolean patchTank(FluidTank tank, String strainId, StrainRegistrySavedData.StrainEntry entry) {
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty()) return false;
        String fluidStrainId = fluid.get(ModDataComponentTypes.STRAIN_ID.get());
        if (!strainId.equals(fluidStrainId)) return false;
        StrainData current = StrainUtil.getStrain(fluid);
        if (current == StrainData.EMPTY) return false;

        FluidStack updated = fluid.copy();
        StrainUtil.setStrain(updated, StrainRegistrySavedData.withEntryApplied(current, entry));
        if (!entry.creatorName().isBlank()) updated.set(ModDataComponentTypes.STRAIN_CREATOR.get(), entry.creatorName());
        tank.setFluid(updated);
        return true;
    }
}

