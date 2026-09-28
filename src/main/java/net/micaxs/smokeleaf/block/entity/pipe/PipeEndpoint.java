package net.micaxs.smokeleaf.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * One IMPORT or EXPORT face of a pipe network — a cached, self-invalidating handle to whatever
 * capability the adjacent (non-pipe) block exposes on that face. Never holds an item/fluid buffer
 * itself; it's purely a live view used by {@link PipeNetworkTickHandler}.
 */
public final class PipeEndpoint {
    public final BlockPos pipePos;
    public final Direction direction;
    public final PipeConnection mode;
    public final BlockCapabilityCache<?> cache;

    public PipeEndpoint(BlockPos pipePos, Direction direction, PipeConnection mode, BlockCapabilityCache<?> cache) {
        this.pipePos = pipePos;
        this.direction = direction;
        this.mode = mode;
        this.cache = cache;
    }

    public BlockPos neighborPos() {
        return pipePos.relative(direction);
    }
}
