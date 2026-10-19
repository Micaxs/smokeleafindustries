package net.micaxs.smokeleaf.block.entity;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.micaxs.smokeleaf.transfer.ModEnergyStorage;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.recipe.SynthesizerRecipe;
import net.micaxs.smokeleaf.recipe.SynthesizerRecipeInput;
import net.micaxs.smokeleaf.screen.custom.SynthesizerMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.micaxs.smokeleaf.transfer.ModItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SynthesizerBlockEntity extends BlockEntity implements MenuProvider {

    // Slot indices
    private static final int DNA_SLOT = 0;
    private static final int REAGENT_SLOT_1 = 1;
    private static final int REAGENT_SLOT_2 = 2;
    private static final int REAGENT_SLOT_3 = 3;
    private static final int OUTPUT_SLOT = 4;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private int progress = 0;
    private int maxProgress = 82;

    protected final ContainerData data;

    public final ModItemStackHandler itemHandler = new ModItemStackHandler(5) {
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
                case REAGENT_SLOT_1, REAGENT_SLOT_2, REAGENT_SLOT_3 -> !stack.is(ModItems.DNA_STRAND);
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

    public SynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYNTHESIZER_BE.get(), pos, state);
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
        return Component.literal("DNA Synthesizer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SynthesizerMenu(id, inv, this, this.data);
    }

    public ResourceHandler<ItemResource> getItemHandler(@Nullable net.minecraft.core.Direction dir) {
        return ExtractRestrictedItemHandler.outputOnly(itemHandler, OUTPUT_SLOT);
    }
    public EnergyHandler getEnergyStorage(@Nullable net.minecraft.core.Direction dir) {
        return ENERGY_STORAGE;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean hasEnergy = ENERGY_STORAGE.getEnergyStored() > 0;

        if (hasEnergy && hasRecipe()) {
            progress++;
            ENERGY_STORAGE.extractEnergy(20, false);

            if (level.getRandom().nextInt(2) == 0) {
                level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
            }

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

    private Optional<RecipeHolder<SynthesizerRecipe>> getCurrentRecipe() {
        SynthesizerRecipeInput input = new SynthesizerRecipeInput(
                itemHandler.getStackInSlot(DNA_SLOT),
                itemHandler.getStackInSlot(REAGENT_SLOT_1),
                itemHandler.getStackInSlot(REAGENT_SLOT_2),
                itemHandler.getStackInSlot(REAGENT_SLOT_3)
        );
        return ModRecipes.find(level, ModRecipes.SYNTHESIZER_TYPE.get(), input);
    }

    private boolean hasRecipe() {
        var opt = getCurrentRecipe();
        if (opt.isEmpty()) return false;
        ItemStack dna = itemHandler.getStackInSlot(DNA_SLOT);
        if (!(dna.getItem() instanceof DNAStrandItem)) return false;

        // Check if it has Ingredients or just do w/e and add them even if invalid strands.
        if (itemHandler.getStackInSlot(REAGENT_SLOT_1).isEmpty()) return false;
        if (itemHandler.getStackInSlot(REAGENT_SLOT_2).isEmpty()) return false;
        if (itemHandler.getStackInSlot(REAGENT_SLOT_3).isEmpty()) return false;

        ItemStack simulated = opt.get().value().assemble(new SynthesizerRecipeInput(
                dna,
                itemHandler.getStackInSlot(REAGENT_SLOT_1),
                itemHandler.getStackInSlot(REAGENT_SLOT_2),
                itemHandler.getStackInSlot(REAGENT_SLOT_3)
        ));

        return canInsertItemIntoOutputSlot(simulated)
                && canInsertAmountIntoOutputSlot(simulated.getCount());
    }

    private void craftItem() {
        var opt = getCurrentRecipe();
        if (opt.isEmpty()) return;

        SynthesizerRecipe recipe = opt.get().value();
        SynthesizerRecipeInput input = new SynthesizerRecipeInput(
                itemHandler.getStackInSlot(DNA_SLOT),
                itemHandler.getStackInSlot(REAGENT_SLOT_1),
                itemHandler.getStackInSlot(REAGENT_SLOT_2),
                itemHandler.getStackInSlot(REAGENT_SLOT_3)
        );

        ItemStack output = recipe.assemble(input);
        if (output.isEmpty()) return;

        // Consume base DNA
        itemHandler.extractItem(DNA_SLOT, 1, false);
        // Consume all 3 reagents (one each)
        if (!itemHandler.getStackInSlot(REAGENT_SLOT_1).isEmpty()) itemHandler.extractItem(REAGENT_SLOT_1, 1, false);
        if (!itemHandler.getStackInSlot(REAGENT_SLOT_2).isEmpty()) itemHandler.extractItem(REAGENT_SLOT_2, 1, false);
        if (!itemHandler.getStackInSlot(REAGENT_SLOT_3).isEmpty()) itemHandler.extractItem(REAGENT_SLOT_3, 1, false);

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
    protected void saveAdditional(ValueOutput output) {
        itemHandler.serialize(output.child("synthesizer.inventory"));
        output.putInt("synthesizer.progress", progress);
        output.putInt("synthesizer.maxProgress", maxProgress);
        output.putInt("synthesizer.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty("synthesizer.inventory"));
        ENERGY_STORAGE.setEnergy(input.getIntOr("synthesizer.energy", 0));
        progress = input.getIntOr("synthesizer.progress", 0);
        maxProgress = input.getIntOr("synthesizer.maxProgress", 0);
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
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
        super.preRemoveSideEffects(pos, state);
    }
}
