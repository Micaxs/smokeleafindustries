package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.recipe.GummyRecipe;
import net.micaxs.smokeleaf.recipe.GummyRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.GummyMachineMenu;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainTankHolder;
import net.micaxs.smokeleaf.strain.StrainTankTracker;
import net.micaxs.smokeleaf.strain.StrainUtil;
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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class GummyMachineBlockEntity extends BlockEntity implements MenuProvider, StrainTankHolder {

    private static final int SLOT_MOLD = 0;
    private static final int SLOT_CATALYST = 1;
    private static final int SLOT_OUTPUT = 2;
    private static final int REQUIRED_OIL_AMOUNT = 250;

    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty() || level == null) return false;
            return switch (slot) {
                case SLOT_MOLD -> isValidMold(stack);
                case SLOT_CATALYST -> isValidCatalyst(stack);
                default -> false;
            };
        }
    };

    private boolean isValidMold(ItemStack stack) {
        if (level == null) return false;
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.GUMMY_TYPE.get()).stream()
                .anyMatch(h -> h.value().mold().test(stack));
    }

    private boolean isValidCatalyst(ItemStack stack) {
        if (level == null) return false;
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.GUMMY_TYPE.get()).stream()
                .anyMatch(h -> h.value().catalyst().ingredient().test(stack));
    }

    public IItemHandler getItemHandler(@Nullable Direction direction) {
        // Also keeps a pipe from being able to yank the reusable mold out from under a running craft.
        return ExtractRestrictedItemHandler.outputOnly(this.itemHandler, SLOT_OUTPUT);
    }

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, 320) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
        }
    };

    private final FluidTank FLUID_TANK = new FluidTank(8000) {
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
            return isOilFluid(stack.getFluid());
        }
    };

    private static boolean isOilFluid(Fluid fluid) {
        return ModFluids.isExtractFluid(fluid);
    }

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public GummyMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GUMMY_MACHINE_BE.get(), pos, blockState);
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
        ItemStack out = itemHandler.getStackInSlot(SLOT_OUTPUT);
        return out.isEmpty() ? itemHandler.getStackInSlot(SLOT_MOLD) : out;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public IFluidHandler getTank(@Nullable Direction direction) {
        return FLUID_TANK;
    }

    private Optional<GummyRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack mold = itemHandler.getStackInSlot(SLOT_MOLD);
        ItemStack catalyst = itemHandler.getStackInSlot(SLOT_CATALYST);
        if (mold.isEmpty() || catalyst.isEmpty()) return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.GUMMY_TYPE.get(), new GummyRecipeInput(mold, catalyst), level)
                .map(RecipeHolder::value);
    }

    private ItemStack buildGummyOutput(FluidStack oil, ItemStack recipeOutput) {
        ItemStack result = new ItemStack(recipeOutput.getItem(), recipeOutput.getCount());
        StrainData strainData = StrainUtil.getStrain(oil);
        if (strainData != null && strainData != StrainData.EMPTY) {
            StrainUtil.setStrain(result, strainData);
            String strainId = oil.get(ModDataComponentTypes.STRAIN_ID.get());
            if (strainId != null) result.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
            String strainCreator = oil.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (strainCreator != null) result.set(ModDataComponentTypes.STRAIN_CREATOR.get(), strainCreator);
        }
        return result;
    }

    private boolean hasEnoughOil() {
        return !FLUID_TANK.isEmpty() && isOilFluid(FLUID_TANK.getFluid().getFluid())
                && FLUID_TANK.getFluidAmount() >= REQUIRED_OIL_AMOUNT;
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack candidate) {
        ItemStack slot = itemHandler.getStackInSlot(SLOT_OUTPUT);
        if (slot.isEmpty()) return true;
        if (slot.getItem() != candidate.getItem()) return false;
        return Objects.equals(StrainUtil.getStrain(slot), StrainUtil.getStrain(candidate));
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStack slot = itemHandler.getStackInSlot(SLOT_OUTPUT);
        int maxCount = slot.isEmpty() ? 64 : slot.getMaxStackSize();
        return maxCount >= slot.getCount() + count;
    }

    private boolean hasRecipe() {
        if (!hasEnoughOil()) return false;
        Optional<GummyRecipe> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;

        ItemStack candidate = buildGummyOutput(FLUID_TANK.getFluid(), recipeOpt.get().output());
        return canInsertAmountIntoOutputSlot(candidate.getCount()) && canInsertItemIntoOutputSlot(candidate);
    }

    private void craftGummy() {
        Optional<GummyRecipe> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty() || !hasEnoughOil()) return;
        GummyRecipe recipe = recipeOpt.get();

        ItemStack candidate = buildGummyOutput(FLUID_TANK.getFluid(), recipe.output());

        FLUID_TANK.drain(REQUIRED_OIL_AMOUNT, IFluidHandler.FluidAction.EXECUTE);

        // The mold in SLOT_MOLD is intentionally never extracted — it is a reusable tool.
        // The catalyst (sugar) IS consumed per-craft.
        itemHandler.extractItem(SLOT_CATALYST, recipe.catalyst().count(), false);

        ItemStack outSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);
        if (outSlot.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, candidate);
        } else {
            ItemStack merged = outSlot.copy();
            merged.grow(candidate.getCount());
            itemHandler.setStackInSlot(SLOT_OUTPUT, merged);
        }
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean hasEnergy = ENERGY_STORAGE.getEnergyStored() > 0;

        if (hasEnergy && hasRecipe()) {
            progress++;
            ENERGY_STORAGE.extractEnergy(20, false);

            if (level.random.nextInt(2) == 0) {
                level.addParticle(ParticleTypes.SMOKE,
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 1.0,
                        blockPos.getZ() + 0.5,
                        0.0, 0.0, 0.0);
            }

            if (progress >= maxProgress) {
                craftGummy();
                progress = 0;
            }
            setChanged(level, blockPos, blockState);
        } else {
            if (progress != 0) {
                progress = 0;
                setChanged(level, blockPos, blockState);
            }
        }

        boolean shouldBePowered = (progress > 0) || (hasEnergy && hasRecipe());
        if (getBlockState().getValue(BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(getBlockPos(),
                    getBlockState().setValue(BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.smokeleafindustries.gummy_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new GummyMachineMenu(i, inv, this, data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        tag.put("gummy_machine.inventory", itemHandler.serializeNBT(regs));
        tag.putInt("gummy_machine.progress", progress);
        tag.putInt("gummy_machine.maxProgress", maxProgress);
        tag.putInt("gummy_machine.energy", ENERGY_STORAGE.getEnergyStored());
        tag = FLUID_TANK.writeToNBT(regs, tag);
        super.saveAdditional(tag, regs);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        itemHandler.deserializeNBT(regs, tag.getCompound("gummy_machine.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("gummy_machine.energy"));
        progress = tag.getInt("gummy_machine.progress");
        maxProgress = tag.getInt("gummy_machine.maxProgress");
        FLUID_TANK.readFromNBT(regs, tag);
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
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
