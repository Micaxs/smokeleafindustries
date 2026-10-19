package net.micaxs.smokeleaf.block.entity;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.micaxs.smokeleaf.transfer.FluidAction;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.micaxs.smokeleaf.transfer.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.WeedFluidStackUtil;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.LiquifierRecipe;
import net.micaxs.smokeleaf.recipe.LiquifierRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.LiquifierMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
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
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainTankHolder;
import net.micaxs.smokeleaf.strain.StrainTankTracker;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.micaxs.smokeleaf.transfer.ModFluidTank;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.micaxs.smokeleaf.transfer.ModItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LiquifierBlockEntity extends BlockEntity implements MenuProvider, StrainTankHolder {

    private static final int INPUT_SLOT = 0;

    public final ModItemStackHandler itemHandler = new ModItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == INPUT_SLOT && isValidInput(stack);
        }
    };

    private boolean isValidInput(ItemStack stack) {
        if (level == null || stack.isEmpty()) return false;
        LiquifierRecipeInput input = new LiquifierRecipeInput(stack);
        return ModRecipes.find(level, ModRecipes.LIQUIFIER_TYPE.get(), input)
                .isPresent();
    }

    public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction direction) {
        // The Liquifier's only item slot is its raw-material input — it's consumed into the fluid
        // tank and never turns into an item output, so nothing should ever be pulled from it.
        return new ExtractRestrictedItemHandler(this.itemHandler, slot -> false);
    }

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, 320) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
        }
    };

    private final ModFluidTank FLUID_TANK = new ModFluidTank(8000) {
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

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 156;

    public LiquifierBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LIQUIFIER_BE.get(), pos, blockState);
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
                switch (i) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                }
            }
            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public ItemStack getRenderStack() {
        return itemHandler.getStackInSlot(INPUT_SLOT);
    }

    public EnergyHandler getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public ResourceHandler<FluidResource> getTank(@Nullable Direction direction) {
        return FLUID_TANK;
    }

    private Optional<LiquifierRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack stack = itemHandler.getStackInSlot(INPUT_SLOT);
        if (stack.isEmpty()) return Optional.empty();
        return ModRecipes.find(level, ModRecipes.LIQUIFIER_TYPE.get(), new LiquifierRecipeInput(stack))
                .map(RecipeHolder::value);
    }

    /**
     * Whether crafting {@code recipe} right now would actually be accepted by the tank. Simulates
     * the real fill (including the strain-tagged output this recipe would produce) rather than
     * just comparing base fluid types — two different strains both liquify to the same underlying
     * {@code unidentified_mixture_fluid} type, differing only by their STRAIN_DATA component, so a
     * type-only check would let a mismatched strain "process" the input for no output (see
     * {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank#fill}, which rejects a
     * fill whose components don't match the tank's current content).
     */
    private boolean hasSpaceFor(LiquifierRecipe recipe) {
        ItemStack in = itemHandler.getStackInSlot(INPUT_SLOT);
        if (in.isEmpty()) return false;
        FluidStack candidate = buildOutputFluid(in, recipe);
        if (candidate.isEmpty()) return false;
        return FLUID_TANK.fill(candidate, FluidAction.SIMULATE) >= candidate.getAmount();
    }

    private void craftFluid(LiquifierRecipe recipe) {
        ItemStack in = itemHandler.extractItem(INPUT_SLOT, 1, false);
        FluidStack out = buildOutputFluid(in, recipe);
        FLUID_TANK.fill(out, FluidAction.EXECUTE);
    }

    /** Builds the exact FluidStack (including inherited effects / STRAIN_DATA) that liquifying {@code in} via {@code recipe} would produce. */
    private FluidStack buildOutputFluid(ItemStack in, LiquifierRecipe recipe) {
        FluidStack out = recipe.outputCopy();

        if (recipe.shouldInheritInputEffects() && !in.isEmpty() && in.getItem() instanceof BaseWeedItem weedItem) {
            MobEffect effect = weedItem.getEffect();
            if (effect != null) {
                Identifier id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
                if (id != null) {
                    int amplifier = 2; // extracts are level 2 in your item definitions
                    int duration = weedItem.getDuration();
                    WeedFluidStackUtil.withWeedData(out, java.util.List.of(id), amplifier, duration);
                }
            }
        }

        // If we are producing the player-made mixture, ensure it carries strain data + effects + stable stat rolls.
        // (MixtureWeedFluidType uses STRAIN_DATA to tint dynamically.)
        if (!in.isEmpty() && out.getFluid() == net.micaxs.smokeleaf.fluid.ModFluids.SOURCE_UNIDENTIFIED_MIXTURE_FLUID.get()) {
            // Read STRAIN_DATA from input item (generic extract with strain data)
            StrainData inputSD = in.get(ModDataComponentTypes.STRAIN_DATA.get());

            // Ensure STRAIN_DATA exists.
            StrainData base;
            if (inputSD != null && inputSD != StrainData.EMPTY) {
                base = inputSD;
            } else if (out.has(ModDataComponentTypes.STRAIN_DATA.get())) {
                base = StrainUtil.getStrain(out);
            } else {
                base = new StrainData(
                        StrainUtil.DEFAULT_UNIDENTIFIED_COLOR, 0xFF4A7A2E,
                        0, 0, 0, 0, 0,
                        java.util.List.of(), 0, 0,
                        false, "",
                        StrainData.TypeColors.NONE,
                        "", ""
                );
            }

            // If we inherited effects, also mirror them into STRAIN_DATA so other consumers can just read strain.
            var weed = WeedFluidStackUtil.getWeedData(out);
            if (weed != null && !weed.effects().isEmpty() && base.effects().isEmpty()) {
                base = new StrainData(
                        base.colorArgb(),
                        base.leafColor(),
                        base.thc(),
                        base.cbd(),
                        base.nitrogen(),
                        base.phosphorus(),
                        base.potassium(),
                        weed.effects(),
                        weed.amplifier(),
                        weed.durationTicks(),
                        base.identified(),
                        base.displayName(),
                        base.typeColors(),
                        base.baseStrain1(),
                        base.baseStrain2()
                );
            }

            // Propagate strain ID from extract to fluid for lineage tracking.
            // If the extract has no explicit STRAIN_ID, derive a deterministic one from content
            // so two extracts with identical stats always share the same strain lineage.
            var strainId = in.get(ModDataComponentTypes.STRAIN_ID.get());
            if (strainId == null && base != StrainData.EMPTY) {
                strainId = StrainUtil.strainContentId(base);
            }
            if (strainId != null) out.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

            // Propagate creator from extract item to fluid so "Discovered by" carries through to the bucket.
            var strainCreator = in.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (strainCreator != null && !strainCreator.isBlank()) {
                out.set(ModDataComponentTypes.STRAIN_CREATOR.get(), strainCreator);
            }

            // Roll THC/CBD + N/P/K once, only if unset — deterministic when strain ID is known.
            base = StrainUtil.finalizeMixtureStats(base, level != null ? level.getRandom() : null, strainId);
            StrainUtil.setStrain(out, base);
        }

        return out;
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean hasEnergy = ENERGY_STORAGE.getEnergyStored() > 0;
        Optional<LiquifierRecipe> recipeOpt = getCurrentRecipe();

        if (hasEnergy && recipeOpt.isPresent() && hasSpaceFor(recipeOpt.get())) {
            progress++;
            ENERGY_STORAGE.extractEnergy(20, false);

            if (level.getRandom().nextInt(2) == 0) {
                level.addParticle(ParticleTypes.SMOKE,
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 1.0,
                        blockPos.getZ() + 0.5,
                        0.0, 0.0, 0.0);
            }

            if (progress >= maxProgress) {
                craftFluid(recipeOpt.get());
                progress = 0;
            }
            setChanged(level, blockPos, blockState);
        } else {
            if (progress != 0) {
                progress = 0;
                setChanged(level, blockPos, blockState);
            }
        }

        boolean shouldBePowered = (progress > 0) || (hasEnergy && recipeOpt.isPresent());
        if (getBlockState().getValue(BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(getBlockPos(),
                    getBlockState().setValue(BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    // Existing fluid push (if still needed)
    private void pushFluidToAboveNeighbour() {
        ResourceHandler<FluidResource> above = level.getCapability(Capabilities.Fluid.BLOCK, worldPosition.above(), Direction.DOWN);
        if (above != null) {
            ResourceHandlerUtil.move(this.FLUID_TANK, above, fluid -> true, Integer.MAX_VALUE, null);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Weed Liquifier");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new LiquifierMenu(i, inv, this, data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        itemHandler.serialize(output.child("liquifier.inventory"));
        output.putInt("liquifier.progress", progress);
        output.putInt("liquifier.maxProgress", maxProgress);
        output.putInt("liquifier.energy", ENERGY_STORAGE.getEnergyStored());
        FLUID_TANK.serialize(output);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty("liquifier.inventory"));
        ENERGY_STORAGE.setEnergy(input.getIntOr("liquifier.energy", 0));
        progress = input.getIntOr("liquifier.progress", 0);
        maxProgress = input.getIntOr("liquifier.maxProgress", 0);
        FLUID_TANK.deserialize(input);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider regs) {
        return saveWithoutMetadata(regs);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
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
        // ModFluidTank#setFluid just overwrites the field directly (unlike fill()/drain(), it never
        // calls onContentsChanged()), so without an explicit sync here the server-side data is
        // correct but no update packet goes out, leaving an already-open GUI/tooltip stale until
        // something else (relog, chunk reload) forces a fresh sync.
        if (level != null && !level.isClientSide()) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
        super.preRemoveSideEffects(pos, state);
    }
}
