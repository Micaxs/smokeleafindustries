package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReflectorBlockEntity extends BlockEntity {
    private static final int GROW_RADIUS = 1;
    private static final int GROW_DEPTH = 5;
    private static final int GROW_TICK_INTERVAL = 100;
    private static final int ATTEMPTS_PER_CYCLE = 1;

    private ItemStack lamp = ItemStack.EMPTY;
    private int tickCounter = 0;

    public ReflectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFLECTOR.get(), pos, state);
    }

    public void setLamp(ItemStack stack) {
        this.lamp = stack.copy();
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState st = getBlockState();
            if (!st.getValue(ReflectorBlock.HAS_LAMP)) {
                level.setBlock(worldPosition, st.setValue(ReflectorBlock.HAS_LAMP, true), Block.UPDATE_CLIENTS);
            }
        }
    }

    public ItemStack getLampStack() {
        return lamp;
    }

    public ItemStack getLamp() {
        return lamp;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ReflectorBlockEntity be) {
        if (be.lamp.isEmpty()) return;

        // Consume 1 durability every tick
        if (be.lamp.isDamageableItem()) {
            int newDamage = be.lamp.getDamageValue() + 1;
            be.lamp.setDamageValue(newDamage);
            if (newDamage >= be.lamp.getMaxDamage()) {
                // Play break sound
                if (!level.isClientSide) {
                    level.playSound(null, pos, ModSounds.LAMP_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                be.lamp = ItemStack.EMPTY;
                if (state.getValue(ReflectorBlock.HAS_LAMP)) {
                    level.setBlock(pos, state.setValue(ReflectorBlock.HAS_LAMP, false), Block.UPDATE_ALL);
                }
                be.setChanged();
                return;
            }
            be.setChanged();
        }

        // Throttled growth logic
        be.tickCounter++;
        if (be.tickCounter < GROW_TICK_INTERVAL) return;
        be.tickCounter = 0;

        if (level instanceof ServerLevel serverLevel) {
            accelerateCrops(serverLevel, pos);
        }
    }

    public int getLampRemainingTicks() {
        if (lamp == null || lamp.isEmpty()) return 0;
        int max = lamp.getMaxDamage();
        int dmg = lamp.getDamageValue();
        int remaining = Math.max(0, max - dmg);
        return remaining;
    }

    public ItemStack extractLamp() {
        if (lamp.isEmpty()) return ItemStack.EMPTY;
        ItemStack out = lamp.copy();
        lamp = ItemStack.EMPTY;
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState st = getBlockState();
            if (st.getValue(ReflectorBlock.HAS_LAMP)) {
                level.setBlock(worldPosition, st.setValue(ReflectorBlock.HAS_LAMP, false), Block.UPDATE_ALL);
            }
        }
        return out;
    }

    private static void accelerateCrops(ServerLevel level, BlockPos origin) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -GROW_RADIUS; dx <= GROW_RADIUS; dx++) {
            for (int dz = -GROW_RADIUS; dz <= GROW_RADIUS; dz++) {
                for (int depth = 1; depth <= GROW_DEPTH; depth++) {
                    cursor.set(origin.getX() + dx, origin.getY() - depth, origin.getZ() + dz);
                    BlockState cropState = level.getBlockState(cursor);
                    if (!(cropState.getBlock() instanceof BaseWeedCropBlock crop)) continue;
                    if (cropState.getValue(crop.getTop())) continue;
                    if (crop.isMaxAge(cropState)) continue;
                    if (!hasVerticalLineOfSight(level, origin, cursor)) continue;
                    candidates.add(cursor.immutable());
                }
            }
        }

        if (candidates.isEmpty()) return;

        RandomSource rand = level.random;
        int attempts = Math.min(ATTEMPTS_PER_CYCLE, candidates.size());
        for (int i = 0; i < attempts; i++) {
            BlockPos targetPos = candidates.get(rand.nextInt(candidates.size()));
            BlockState st = level.getBlockState(targetPos);
            if (!(st.getBlock() instanceof BaseWeedCropBlock crop)) continue;
            float f = crop.getLocalGrowthSpeed(level, targetPos);
            int bound = (int)(25.0F / f) + 1;
            if (rand.nextInt(bound) == 0) {
                growOneStageTall(level, targetPos, st, crop);
            }
        }
    }

    private static boolean hasVerticalLineOfSight(Level level, BlockPos from, BlockPos to) {
        int topY = from.getY() - 1;
        for (int y = topY; y > to.getY(); y--) {
            BlockPos mid = new BlockPos(to.getX(), y, to.getZ());
            BlockState bs = level.getBlockState(mid);
            if (bs.canOcclude()) return false;
        }
        return true;
    }

    private static void growOneStageTall(ServerLevel level, BlockPos bottomPos, BlockState bottomState, BaseWeedCropBlock crop) {
        IntegerProperty ageProp = crop.getAgeProperty();
        BooleanProperty topProp = crop.getTop();
        int age = bottomState.getValue(ageProp);
        int max = crop.getMaxAge();
        if (age >= max) return;
        int newAge = Math.min(age + 1, max);
        BlockState newBottom = bottomState.setValue(ageProp, newAge);
        level.setBlock(bottomPos, newBottom, Block.UPDATE_CLIENTS);
        int tallAge = crop.getTallAge();
        BlockPos topPos = bottomPos.above();
        if (newAge >= tallAge) {
            BlockState topState = level.getBlockState(topPos);
            if (topState.getBlock() == crop) {
                level.setBlock(topPos,
                        topState.setValue(ageProp, newAge).setValue(topProp, true),
                        Block.UPDATE_CLIENTS);
            } else if (topState.isAir()) {
                BlockState createdTop = newBottom.setValue(topProp, true);
                level.setBlock(topPos, createdTop, Block.UPDATE_CLIENTS);
            }
        }
    }

    public void dropContents() {
        if (!lamp.isEmpty() && level != null) {
            Containers.dropItemStack(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    lamp);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            BlockState st = getBlockState();
            boolean hasLamp = !lamp.isEmpty();
            if (st.getValue(ReflectorBlock.HAS_LAMP) != hasLamp) {
                level.setBlock(worldPosition, st.setValue(ReflectorBlock.HAS_LAMP, hasLamp), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!lamp.isEmpty()) {
            tag.put("Lamp", lamp.save(new CompoundTag()));
        }
        tag.putInt("TickCounter", tickCounter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lamp = tag.contains("Lamp") ? ItemStack.of(tag.getCompound("Lamp")) : ItemStack.EMPTY;
        tickCounter = tag.getInt("TickCounter");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}
