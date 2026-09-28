package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.GrowLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

public class GrowLightBlockEntity extends BlockEntity {
    private static final int GROW_RADIUS = 1;
    private static final int GROW_DEPTH = 5;
    private static final int GROW_TICK_INTERVAL = 100;
    private static final int ATTEMPTS_PER_CYCLE = 2;

    private int tickCounter = 0;

    public GrowLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROW_LIGHT.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GrowLightBlockEntity be) {
        if (!state.getValue(GrowLightBlock.CLICKED)) return;

        be.tickCounter++;
        if (be.tickCounter < GROW_TICK_INTERVAL) return;
        be.tickCounter = 0;

        if (level instanceof ServerLevel sl) {
            accelerateCrops(sl, pos);
        }
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("TickCounter", tickCounter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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
