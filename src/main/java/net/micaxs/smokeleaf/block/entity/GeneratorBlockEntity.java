package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.block.entity.energy.ModEnergyUtil;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.GeneratorRecipe;
import net.micaxs.smokeleaf.recipe.GeneratorRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.screen.custom.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
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

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private static final int INPUT_SLOT = 0;
    private static final int ENERGY_PER_TICK = GeneratorRecipe.ENERGY_PER_TICK;
    private static final int ENERGY_PUSH_PER_DIRECTION = 40;

    private int generationPerTick = ENERGY_PER_TICK;

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int remainingEnergy = 0; // energy left in current burn

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
            }
        }
    };

    protected final ContainerData data;

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, ENERGY_PER_TICK) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
            }
        };
    }

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> burnTime;
                    case 1 -> maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> burnTime = value;
                    case 1 -> maxBurnTime = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return itemHandler;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return ENERGY_STORAGE;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Weed Generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GeneratorMenu(id, inv, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;
        boolean hasSpace = ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored();

        if (hasSpace) {
            if (burnTime <= 0 && remainingEnergy <= 0) {
                ItemStack stack = itemHandler.getStackInSlot(INPUT_SLOT);
                Optional<GeneratorRecipe> opt = getRecipe(stack);
                if (opt.isPresent()) {
                    GeneratorRecipe recipe = opt.get();
                    remainingEnergy = recipe.totalEnergy();
                    burnTime = recipe.computedBurnTime();
                    maxBurnTime = burnTime;
                    generationPerTick = ENERGY_PER_TICK;
                    itemHandler.extractItem(INPUT_SLOT, 1, false);

                    // If the item has a crafting remaining item, give it back to the input slot.
                    if (stack.getItem().getCraftingRemainingItem() != null) {
                        ItemStack remaining = stack.getItem().getCraftingRemainingItem().getDefaultInstance();
                        if (!remaining.isEmpty()) {
                            itemHandler.insertItem(INPUT_SLOT, remaining, false);
                        }
                    }

                    changed = true;
                }
            } else if (remainingEnergy > 0) {
                int toGenerate = Math.min(generationPerTick, remainingEnergy);
                int accepted = ENERGY_STORAGE.receiveEnergy(toGenerate, false);
                if (accepted > 0) {
                    remainingEnergy -= accepted;
                    if (burnTime > 0) burnTime--;
                    changed = true;
                }
                if (remainingEnergy <= 0) {
                    burnTime = 0;
                }
            }
        }

        if (changed) {
            setChanged(level, pos, state);
        }

        pushEnergyToNeighbours();

        boolean canStart = hasSpace && remainingEnergy <= 0 && getRecipe(itemHandler.getStackInSlot(INPUT_SLOT)).isPresent();
        boolean shouldPower = burnTime > 0 || canStart;
        BlockState current = getBlockState();
        if (current.hasProperty(BlockStateProperties.POWERED) &&
                current.getValue(BlockStateProperties.POWERED) != shouldPower) {
            level.setBlockAndUpdate(getBlockPos(), current.setValue(BlockStateProperties.POWERED, shouldPower));
        }
    }

    private Optional<GeneratorRecipe> getRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) return Optional.empty();
        GeneratorRecipeInput input = new GeneratorRecipeInput(stack);
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.GENERATOR_TYPE.get(), input, level)
                .map(RecipeHolder::value);
    }

    private void pushEnergyToNeighbours() {
        if (level == null) return;
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = worldPosition.relative(dir);
            if (ModEnergyUtil.doesBlockHaveEnergyStorage(neighbor, level)) {
                ModEnergyUtil.move(worldPosition, neighbor, ENERGY_PUSH_PER_DIRECTION, level);
            }
        }
    }

    public int getConfiguredBurnTimeForDisplay() {
        return maxBurnTime;
    }

    public boolean canBurn(ItemStack stack) {
        return stack.getItem() instanceof BaseWeedItem || stack.getItem() instanceof BaseBudItem;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("generator.inventory", itemHandler.serializeNBT(provider));
        tag.putInt("generator.burnTime", burnTime);
        tag.putInt("generator.maxBurnTime", maxBurnTime);
        tag.putInt("generator.remainingEnergy", remainingEnergy);
        tag.putInt("generator.genPerTick", generationPerTick);
        tag.putInt("generator.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound("generator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("generator.energy"));
        burnTime = tag.getInt("generator.burnTime");
        maxBurnTime = tag.getInt("generator.maxBurnTime");
        remainingEnergy = tag.getInt("generator.remainingEnergy");
        generationPerTick = tag.getInt("generator.genPerTick");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
    }
}