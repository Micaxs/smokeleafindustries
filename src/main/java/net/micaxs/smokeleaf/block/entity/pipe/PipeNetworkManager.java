package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-{@link ServerLevel} owner of every {@link PipeNetwork}. Networks are lazily flood-filled and
 * memoized directly on each member {@link PipeBlockEntity} ({@code networkRef[type]}); this manager
 * only holds the small set of pipe positions that currently have at least one IMPORT/EXPORT
 * connection ("endpoint-bearing" pipes) so {@code PipeNetworkTickHandler} can find active networks
 * in O(endpoint-bearing pipes), never O(all pipes).
 *
 * <p>All topology-changing mutations (place/break/PIPE↔NONE toggle) must go through
 * {@link #invalidateTopology}, which is O(1)-O(6). A capability-neighbor endpoint change
 * (NONE/IMPORT/EXPORT cycling against a machine, not another pipe) never touches pipe topology and
 * is handled by {@link #updateEndpoint}, which patches the already-cached network's endpoint list
 * directly in O(1) instead of triggering a rebuild.
 */
public final class PipeNetworkManager {
    private static final Map<ResourceKey<Level>, PipeNetworkManager> INSTANCES = new HashMap<>();

    public static PipeNetworkManager get(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level.dimension(), k -> new PipeNetworkManager());
    }

    public static void clearAll() {
        INSTANCES.clear();
    }

    private final EnumMap<PipeType, Set<BlockPos>> endpointBearingPositions = new EnumMap<>(PipeType.class);

    private PipeNetworkManager() {
        for (PipeType type : PipeType.VALUES) {
            endpointBearingPositions.put(type, new HashSet<>());
        }
    }

    public Set<BlockPos> getEndpointBearingPositions(PipeType type) {
        return endpointBearingPositions.get(type);
    }

    public void setEndpointBearing(PipeType type, BlockPos pos, boolean bearing) {
        if (bearing) {
            endpointBearingPositions.get(type).add(pos.immutable());
        } else {
            endpointBearingPositions.get(type).remove(pos);
        }
    }

    /** O(1) if already cached; otherwise a single BFS that memoizes the result on every member pipe. */
    public PipeNetwork getOrBuild(ServerLevel level, BlockPos pos, PipeType type) {
        if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) || !pipe.hasType(type)) {
            return null;
        }
        PipeNetwork cached = pipe.getNetworkRef(type);
        if (cached != null) {
            return cached;
        }
        return buildNetwork(level, pos, type);
    }

    private PipeNetwork buildNetwork(ServerLevel level, BlockPos start, PipeType type) {
        PipeNetwork network = new PipeNetwork(type);
        Deque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start.immutable());

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) || !pipe.hasType(type)) continue;

            network.pipePositions.add(pos);
            pipe.setNetworkRef(type, network);

            for (Direction dir : Direction.values()) {
                PipeConnection mode = pipe.getConnection(type, dir);
                BlockPos neighborPos = pos.relative(dir);

                if (mode == PipeConnection.PIPE) {
                    if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity neighborPipe
                            && neighborPipe.hasType(type)
                            && neighborPipe.getConnection(type, dir.getOpposite()) == PipeConnection.PIPE
                            && !visited.contains(neighborPos)) {
                        queue.add(neighborPos.immutable());
                    }
                } else if (mode.isEndpoint()) {
                    network.endpoints.add(new PipeEndpoint(pos, dir, mode, createCache(type, level, neighborPos, dir.getOpposite())));
                }
            }
        }
        return network;
    }

    /**
     * Topology change (type added/removed, or a PIPE link severed/joined) — nulls the cached network
     * at {@code pos} and all 6 neighbors for {@code type}, and refreshes endpoint-bearing bookkeeping
     * for each of those positions. O(1)-O(6); callers never need to touch bearing bookkeeping directly.
     */
    public void invalidateTopology(ServerLevel level, BlockPos pos, PipeType type) {
        invalidateOne(level, pos, type);
        for (Direction dir : Direction.values()) {
            invalidateOne(level, pos.relative(dir), type);
        }
    }

    private void invalidateOne(ServerLevel level, BlockPos pos, PipeType type) {
        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) {
            pipe.setNetworkRef(type, null);
            setEndpointBearing(type, pos, pipe.hasAnyEndpointConnection(type));
        }
    }

    /**
     * A NONE/IMPORT/EXPORT change against a capability-providing neighbor (not another pipe) never
     * changes pipe-to-pipe topology — patch the one affected endpoint directly in the already-cached
     * network (if built) instead of invalidating anything. O(1).
     */
    public void updateEndpoint(ServerLevel level, BlockPos pos, PipeType type, Direction dir, PipeConnection newMode) {
        if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity pipe)) return;

        PipeNetwork network = pipe.getNetworkRef(type);
        if (network != null) {
            network.endpoints.removeIf(e -> e.pipePos.equals(pos) && e.direction == dir);
            if (newMode.isEndpoint()) {
                BlockPos neighborPos = pos.relative(dir);
                network.endpoints.add(new PipeEndpoint(pos, dir, newMode, createCache(type, level, neighborPos, dir.getOpposite())));
            }
        }
        setEndpointBearing(type, pos, pipe.hasAnyEndpointConnection(type));
    }

    private static BlockCapabilityCache<?, Direction> createCache(PipeType type, ServerLevel level, BlockPos neighborPos, Direction context) {
        switch (type) {
            case ITEM:
                return BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level, neighborPos, context);
            case FLUID:
                return BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, level, neighborPos, context);
            case ENERGY:
                return BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, level, neighborPos, context);
            default:
                throw new IllegalStateException("Unknown pipe type: " + type);
        }
    }
}
