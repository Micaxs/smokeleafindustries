package net.micaxs.smokeleaf.block.entity.pipe;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A connected component of same-{@link PipeType} pipes, computed lazily by {@link PipeNetworkManager}
 * and memoized on every member {@code PipeBlockEntity} until a topology change invalidates it. Never
 * recomputed on a timer — this is the object that makes long, mostly-passive pipe runs effectively
 * free: ticking only ever touches {@link #endpoints}, never {@link #pipePositions}.
 */
public final class PipeNetwork {
    public final PipeType type;
    public final Set<BlockPos> pipePositions = new HashSet<>();
    public final List<PipeEndpoint> endpoints = new ArrayList<>();
    public int roundRobinCursor = 0;

    public PipeNetwork(PipeType type) {
        this.type = type;
    }

    public List<PipeEndpoint> exports() {
        List<PipeEndpoint> out = new ArrayList<>();
        for (PipeEndpoint e : endpoints) if (e.mode == PipeConnection.EXPORT) out.add(e);
        return out;
    }

    public List<PipeEndpoint> imports() {
        List<PipeEndpoint> out = new ArrayList<>();
        for (PipeEndpoint e : endpoints) if (e.mode == PipeConnection.IMPORT) out.add(e);
        return out;
    }
}
