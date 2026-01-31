// Java
package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.recipe.DryingRecipe;
import net.micaxs.smokeleaf.recipe.DryingRecipeInput;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Optional;

public class DryingRackBlockEntity extends BlockEntity {

    public static final int SLOT_COUNT = 12;
    private final ItemStack[] items = new ItemStack[SLOT_COUNT];
    private final int[] progress = new int[SLOT_COUNT];

    private static final DustParticleOptions DRYING_PARTICLE =
            new DustParticleOptions(new Vector3f(0.4f, 0.4f, 0.4f), 0.5f);

    private static final float[] LAYER_Y = {3 / 16f, 9 / 16f, 15 / 16f};
    private static final float[][] QUAD_OFFSETS = {
            {-0.25f,  0.25f},
            { 0.25f,  0.25f},
            {-0.25f, -0.25f},
            { 0.25f, -0.25f}
    };

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE.get(), pos, state);
        Arrays.fill(items, ItemStack.EMPTY);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity be) {
        if (!(level instanceof ServerLevel server)) return;

        boolean changed = false;
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);

        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = be.items[i];
            if (stack.isEmpty()) {
                if (be.progress[i] != 0) {
                    be.progress[i] = 0;
                    changed = true;
                }
                continue;
            }

            Optional<RecipeHolder<DryingRecipe>> recipeHolderOpt = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.DRYING_TYPE.get(), new DryingRecipeInput(stack), level);

            if (recipeHolderOpt.isEmpty()) {
                be.progress[i] = 0;
                continue;
            }

            DryingRecipe recipe = recipeHolderOpt.get().value();

            if (recipe.dryBud() && stack.getItem() instanceof BaseBudItem budItem) {
                // Skip everything (including particles) if already dry
                if (be.isDry(stack)) continue;

                // Emit particle only while still drying
                if (server.random.nextFloat() < 0.10f) {
                    float[] off = be.getSlotRenderPosition(i, facing);
                    server.sendParticles(
                            DRYING_PARTICLE,
                            pos.getX() + 0.5 + off[0],
                            pos.getY() + 0.10 + off[1],
                            pos.getZ() + 0.5 + off[2],
                            1, 0.0, 0.0, 0.0, 0.0
                    );
                }

                be.progress[i]++;
                int needed = Math.max(1, budItem.dryingTime);
                if (be.progress[i] >= needed) {
                    BaseBudItem.changeDryStatus(stack, true);
                    be.progress[i] = 0;
                    changed = true;
                }
            } else {
                // Non\-bud drying recipes still emit particles normally
                if (server.random.nextFloat() < 0.10f) {
                    float[] off = be.getSlotRenderPosition(i, facing);
                    server.sendParticles(
                            DRYING_PARTICLE,
                            pos.getX() + 0.5 + off[0],
                            pos.getY() + 0.10 + off[1],
                            pos.getZ() + 0.5 + off[2],
                            1, 0.0, 0.0, 0.0, 0.0
                    );
                }

                be.progress[i]++;
                int needed = Math.max(1, recipe.time());
                if (be.progress[i] >= needed) {
                    if (!recipe.result().isEmpty()) {
                        be.items[i] = recipe.result().copy();
                    }
                    be.progress[i] = 0;
                    changed = true;
                }
            }
        }

        if (changed) be.setChangedAndSync();
    }

    public int getProgressForSlot(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        return progress[slot];
    }

    // Compute total time (ticks) needed for the current item in the slot, or 0 if no recipe
    public int getTotalTimeForSlot(Level level, int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        ItemStack stack = items[slot];
        if (stack.isEmpty()) return 0;

        Optional<RecipeHolder<DryingRecipe>> recipeHolderOpt =
                level.getRecipeManager().getRecipeFor(ModRecipes.DRYING_TYPE.get(), new DryingRecipeInput(stack), level);
        if (recipeHolderOpt.isEmpty()) return 0;

        DryingRecipe recipe = recipeHolderOpt.get().value();
        if (recipe.dryBud() && stack.getItem() instanceof BaseBudItem budItem) {
            return Math.max(1, budItem.dryingTime);
        }
        return Math.max(1, recipe.time());
    }

    private boolean isDry(ItemStack stack) {
        Boolean v = stack.get(ModDataComponentTypes.DRY);
        return v != null && v;
    }

    public int firstFreeSlotBottomUp() {
        for (int i = 0; i < SLOT_COUNT; i++) if (items[i].isEmpty()) return i;
        return -1;
    }

    public boolean insertOne(ItemStack stack) {
        if (stack.isEmpty()) return false;
        int slot = firstFreeSlotBottomUp();
        if (slot < 0) return false;
        items[slot] = stack.split(1);
        progress[slot] = 0;
        setChangedAndSync();
        return true;
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;
        return items[slot];
    }

    public float[] getSlotRenderPosition(int slot, Direction facing) {
        int layer = slot / 4;
        int quad = slot % 4;
        float y = LAYER_Y[layer];
        float ox = QUAD_OFFSETS[quad][0];
        float oz = QUAD_OFFSETS[quad][1];
        float rx = ox;
        float rz = oz;
        switch (facing) {
            case SOUTH -> { rx = -ox; rz = -oz; }
            case WEST  -> { rx =  oz; rz = -ox; }
            case EAST  -> { rx = -oz; rz =  ox; }
            default -> {}
        }
        return new float[]{rx, y, rz};
    }

    // Determine which vertical layer (0..2) was clicked. Returns -1 if outside.
    public int computeLayerFromHit(BlockHitResult hit) {
        if (hit == null) return -1;
        double localY = hit.getLocation().y - worldPosition.getY();
        if (localY < 0 || localY > 1.0001) return -1;
        if (localY < 0.375) return 0;      // midpoint between centers of layer 0 and 1
        if (localY < 0.75) return 1;       // midpoint between centers of layer 1 and 2
        return 2;
    }

    // Remove the last (right-most / higher index) non-empty slot in a layer.
    public ItemStack removeLastInRow(int layer) {
        if (layer < 0 || layer > 2) return ItemStack.EMPTY;
        int start = layer * 4;
        for (int i = start + 3; i >= start; i--) {
            if (!items[i].isEmpty()) {
                ItemStack ret = items[i];
                items[i] = ItemStack.EMPTY;
                progress[i] = 0;
                setChangedAndSync();
                return ret;
            }
        }
        return ItemStack.EMPTY;
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void drops() {
        if (level == null || level.isClientSide) return;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            }
        }
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < SLOT_COUNT; i++) {
            CompoundTag slotTag = new CompoundTag();
            if (!items[i].isEmpty()) slotTag.put("stack", items[i].save(registries));
            slotTag.putInt("prog", progress[i]);
            tag.put("S" + i, slotTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < SLOT_COUNT; i++) {
            CompoundTag slotTag = tag.getCompound("S" + i);
            if (slotTag.contains("stack")) {
                items[i] = ItemStack.parse(registries, slotTag.getCompound("stack")).orElse(ItemStack.EMPTY);
            } else {
                items[i] = ItemStack.EMPTY;
            }
            progress[i] = slotTag.getInt("prog");
        }
    }

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