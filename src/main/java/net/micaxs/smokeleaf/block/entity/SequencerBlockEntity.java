package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.recipe.*;
import net.micaxs.smokeleaf.screen.custom.SequencerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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

public class SequencerBlockEntity extends BlockEntity implements MenuProvider {

    // Slot indices
    private static final int DNA_SLOT = 0;
    private static final int BASE_EXTRACT = 1;
    private static final int OUTPUT_SLOT = 2;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private int progress = 0;
    private int maxProgress = 82;

    protected final ContainerData data;

    public final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case DNA_SLOT -> stack.is(ModItems.DNA_STRAND);
                case BASE_EXTRACT -> stack.is(ModItems.BASE_EXTRACT);
                case OUTPUT_SLOT -> false;
                default -> false;
            };
        }
    };

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
        }
    };

    public SequencerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEQUENCER_BE.get(), pos, state);
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

    @Override
    public Component getDisplayName() {
        return Component.literal("DNA Sequencer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SequencerMenu(id, inv, this, this.data);
    }

    public IItemHandler getItemHandler(@Nullable net.minecraft.core.Direction dir) {
        return ExtractRestrictedItemHandler.outputOnly(itemHandler, OUTPUT_SLOT);
    }
    public IEnergyStorage getEnergyStorage(@Nullable net.minecraft.core.Direction dir) {
        return ENERGY_STORAGE;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return; // Prevent client-side reset jitter

        boolean hasEnergy = ENERGY_STORAGE.getEnergyStored() > 0;
        if (hasEnergy && hasRecipe()) {
            progress++;
            ENERGY_STORAGE.extractEnergy(20, false);
            if (progress >= maxProgress) {
                craftItem();
                progress = 0;
            }
        } else {
            progress = 0;
        }

        setChanged(level, pos, state);
        boolean powered = (progress > 0) || (hasEnergy && hasRecipe());
        if (state.getValue(BlockStateProperties.POWERED) != powered) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, powered));
        }
    }

    private Optional<RecipeHolder<SequencerRecipe>> getCurrentRecipe() {
        SequencerRecipeInput input = new SequencerRecipeInput(
                itemHandler.getStackInSlot(DNA_SLOT),
                itemHandler.getStackInSlot(BASE_EXTRACT)
        );
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.SEQUENCER_TYPE.get(), input, level);
    }

    private boolean hasRecipe() {
        var opt = getCurrentRecipe();
        if (opt.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack dna = itemHandler.getStackInSlot(DNA_SLOT);
                ItemStack base = itemHandler.getStackInSlot(BASE_EXTRACT);
            }
            return false;
        }

        ItemStack outputSimulated = opt.get().value().assemble(
                new SequencerRecipeInput(itemHandler.getStackInSlot(DNA_SLOT), itemHandler.getStackInSlot(BASE_EXTRACT)), level.registryAccess()
        );

        return canInsertItemIntoOutputSlot(outputSimulated)  && canInsertAmountIntoOutputSlot(outputSimulated.getCount());
    }

    private void craftItem() {
        var opt = getCurrentRecipe();
        if (opt.isEmpty()) return;
        SequencerRecipe recipe = opt.get().value();
        ItemStack output = recipe.assemble(
                new SequencerRecipeInput(itemHandler.getStackInSlot(DNA_SLOT),
                        itemHandler.getStackInSlot(BASE_EXTRACT)),
                level.registryAccess());
        if (output.isEmpty()) return;

        // Consume Inputs
        itemHandler.extractItem(BASE_EXTRACT, 1, false);
        itemHandler.extractItem(DNA_SLOT, 1, false);

        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, output);
        } else if (ItemStack.isSameItemSameComponents(existing, output)) {
            existing.grow(output.getCount());
        }
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack stack) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, stack);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) return count <= 64;
        return existing.getCount() + count <= existing.getMaxStackSize();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("sequencer.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("sequencer.progress", progress);
        tag.putInt("sequencer.maxProgress", maxProgress);
        tag.putInt("sequencer.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("sequencer.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("sequencer.energy"));
        progress = tag.getInt("sequencer.progress");
        maxProgress = tag.getInt("sequencer.maxProgress");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookup) {
        super.onDataPacket(net, pkt, lookup);
    }
}