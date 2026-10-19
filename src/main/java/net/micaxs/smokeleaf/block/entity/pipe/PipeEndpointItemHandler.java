package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Exposed on a pipe's IMPORT/EXPORT item face so external (non-pipe) machines/mods can interact with
 * the whole network through this one face — a live view, never a buffer. On insert/extract it simply
 * tries every *other* endpoint of the same network once; the pipe itself stores nothing.
 */
public final class PipeEndpointItemHandler implements ResourceHandler<ItemResource> {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointItemHandler(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Nullable
    private PipeNetwork network() {
        if (!(pipe.getLevel() instanceof ServerLevel level)) return null;
        return PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ITEM);
    }

    private boolean isSelf(PipeEndpoint endpoint) {
        return endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static ResourceHandler<ItemResource> handlerOf(PipeEndpoint endpoint) {
        return endpoint.cache.getCapability() instanceof ResourceHandler<?> handler ? (ResourceHandler<ItemResource>) handler : null;
    }

    @Override
    public int size() {
        return 1;
    }

    /** Shows the first item any other endpoint could hand out, so generic "extract anything" callers work. */
    @Override
    public ItemResource getResource(int index) {
        PipeNetwork network = network();
        if (network == null) return ItemResource.EMPTY;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<ItemResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty()) return resource;
            }
        }
        return ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return getResource(index).isEmpty() ? 0 : 1;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return 64;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return true;
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (network == null) return 0;

        int inserted = 0;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<ItemResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            inserted += ResourceHandlerUtil.insertStacking(handler, resource, amount - inserted, transaction);
            if (inserted >= amount) break;
        }
        return inserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (network == null) return 0;

        int extracted = 0;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            ResourceHandler<ItemResource> handler = handlerOf(endpoint);
            if (handler == null) continue;
            extracted += handler.extract(resource, amount - extracted, transaction);
            if (extracted >= amount) break;
        }
        return extracted;
    }
}
