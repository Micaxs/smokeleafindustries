package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/** Fluid analog of {@link PipeEndpointItemHandler} — see its javadoc. */
public final class PipeEndpointFluidHandler implements ResourceHandler<FluidResource> {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointFluidHandler(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Nullable
    private PipeNetwork network() {
        if (!(pipe.getLevel() instanceof ServerLevel level)) return null;
        return PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.FLUID);
    }

    private boolean isSelf(PipeEndpoint endpoint) {
        return endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static ResourceHandler<FluidResource> handlerOf(PipeEndpoint endpoint) {
        return endpoint.cache.getCapability() instanceof ResourceHandler<?> handler ? (ResourceHandler<FluidResource>) handler : null;
    }

    @Override
    public int size() {
        return 1;
    }

    /** Shows the first fluid any other endpoint could hand out, so generic "drain anything" callers work. */
    @Override
    public FluidResource getResource(int index) {
        PipeNetwork network = network();
        if (network == null) return FluidResource.EMPTY;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<FluidResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            for (int i = 0; i < handler.size(); i++) {
                FluidResource resource = handler.getResource(i);
                if (!resource.isEmpty()) return resource;
            }
        }
        return FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return getResource(index).isEmpty() ? 0 : 1;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return 1000;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return true;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (network == null) return 0;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<FluidResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            int filled = handler.insert(resource, amount, transaction);
            if (filled > 0) return filled;
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (network == null) return 0;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<FluidResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            int drained = handler.extract(resource, amount, transaction);
            if (drained > 0) return drained;
        }
        return 0;
    }
}
