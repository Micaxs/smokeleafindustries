package net.micaxs.smokeleaf.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * Backport of NeoForge's {@code BlockCapabilityCache}: a cached handle to the capability a block
 * exposes on one face. The cached handler is dropped when its LazyOptional is invalidated, or when
 * the block entity at the position changes or is removed, and is re-resolved on next access.
 */
public final class BlockCapabilityCache<T> {
    private final Capability<T> capability;
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction context;

    @Nullable private BlockEntity cachedBlockEntity;
    @Nullable private LazyOptional<T> cachedOptional;

    private BlockCapabilityCache(Capability<T> capability, ServerLevel level, BlockPos pos, Direction context) {
        this.capability = capability;
        this.level = level;
        this.pos = pos.immutable();
        this.context = context;
    }

    public static <T> BlockCapabilityCache<T> create(Capability<T> capability, ServerLevel level, BlockPos pos, Direction context) {
        return new BlockCapabilityCache<>(capability, level, pos, context);
    }

    @Nullable
    public T getCapability() {
        if (!level.isLoaded(pos)) {
            clear();
            return null;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            clear();
            return null;
        }
        if (be != cachedBlockEntity || be.isRemoved() || cachedOptional == null || !cachedOptional.isPresent()) {
            cachedBlockEntity = be;
            cachedOptional = be.isRemoved() ? LazyOptional.empty() : be.getCapability(capability, context);
        }
        return cachedOptional.orElse(null);
    }

    private void clear() {
        cachedBlockEntity = null;
        cachedOptional = null;
    }
}
