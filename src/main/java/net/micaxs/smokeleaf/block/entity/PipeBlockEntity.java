package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.pipe.PipeConnection;
import net.micaxs.smokeleaf.block.entity.pipe.PipeEndpointEnergyStorage;
import net.micaxs.smokeleaf.block.entity.pipe.PipeEndpointFluidHandler;
import net.micaxs.smokeleaf.block.entity.pipe.PipeEndpointItemHandler;
import net.micaxs.smokeleaf.block.entity.pipe.PipeGeometry;
import net.micaxs.smokeleaf.block.entity.pipe.PipeHitPart;
import net.micaxs.smokeleaf.block.entity.pipe.PipeNetwork;
import net.micaxs.smokeleaf.block.entity.pipe.PipeNetworkManager;
import net.micaxs.smokeleaf.block.entity.pipe.PipeRenderState;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A single block position that can hold up to one segment of each {@link PipeType} (item, fluid,
 * energy) at once, each with its own per-direction {@link PipeConnection} state. Deliberately holds
 * NONE of this in {@code BlockState} — see the pipe system plan for why (state-table combinatorial
 * explosion) — it's exposed to the renderer via {@link #getModelData()} instead.
 */
public class PipeBlockEntity extends BlockEntity {

    public static final ModelProperty<PipeRenderState> RENDER_STATE = new ModelProperty<>();

    private final boolean[] present = new boolean[PipeType.VALUES.length];
    private final PipeConnection[][] connections = new PipeConnection[PipeType.VALUES.length][6];

    // Not persisted — rebuilt lazily. "transient" is used here purely as a documentation marker;
    // Minecraft NBT save/load is manual (saveAdditional/loadAdditional), not java.io.Serializable.
    private final PipeNetwork[] networkRef = new PipeNetwork[PipeType.VALUES.length];
    private VoxelShape cachedShape = Shapes.empty();
    private boolean cachedHasAnyActiveEndpoint = false;

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_BE.get(), pos, state);
        for (PipeConnection[] row : connections) {
            java.util.Arrays.fill(row, PipeConnection.NONE);
        }
    }

    // -----------------------------------------------------------------------
    // Presence / connection state
    // -----------------------------------------------------------------------

    public boolean hasType(PipeType type) {
        return present[type.ordinal()];
    }

    public boolean isEmpty() {
        for (boolean b : present) if (b) return false;
        return true;
    }

    public int presentCount() {
        int c = 0;
        for (boolean b : present) if (b) c++;
        return c;
    }

    public PipeConnection getConnection(PipeType type, Direction dir) {
        return connections[type.ordinal()][dir.ordinal()];
    }

    public boolean hasAnyEndpointConnection(PipeType type) {
        if (!present[type.ordinal()]) return false;
        for (PipeConnection c : connections[type.ordinal()]) {
            if (c.isEndpoint()) return true;
        }
        return false;
    }

    /** Adds {@code type} if not already present, auto-linking (PIPE) to any same-type neighbor pipes. Returns false if already present. */
    public boolean addType(ServerLevel level, PipeType type) {
        if (present[type.ordinal()]) return false;
        present[type.ordinal()] = true;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity neighbor && neighbor.hasType(type)) {
                connections[type.ordinal()][dir.ordinal()] = PipeConnection.PIPE;
                neighbor.connections[type.ordinal()][dir.getOpposite().ordinal()] = PipeConnection.PIPE;
                neighbor.recomputeCaches();
                neighbor.setChangedAndSync();
            }
        }

        recomputeCaches();
        setChangedAndSync();
        PipeNetworkManager.get(level).invalidateTopology(level, worldPosition, type);
        return true;
    }

    /** Removes {@code type} entirely (all its connections go with it), severing any pipe links cleanly. */
    public void removeType(ServerLevel level, PipeType type) {
        if (!present[type.ordinal()]) return;

        for (Direction dir : Direction.values()) {
            if (connections[type.ordinal()][dir.ordinal()] == PipeConnection.PIPE) {
                BlockPos neighborPos = worldPosition.relative(dir);
                if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity neighbor && neighbor.hasType(type)) {
                    neighbor.connections[type.ordinal()][dir.getOpposite().ordinal()] = PipeConnection.NONE;
                    neighbor.recomputeCaches();
                    neighbor.setChangedAndSync();
                }
            }
        }

        present[type.ordinal()] = false;
        java.util.Arrays.fill(connections[type.ordinal()], PipeConnection.NONE);
        recomputeCaches();
        setChangedAndSync();
        PipeNetworkManager.get(level).invalidateTopology(level, worldPosition, type);
    }

    /** The single entry point for wrench-driven connection changes; handles its own network invalidation. */
    public void setConnection(ServerLevel level, PipeType type, Direction dir, PipeConnection newMode) {
        PipeConnection old = connections[type.ordinal()][dir.ordinal()];
        if (old == newMode) return;

        connections[type.ordinal()][dir.ordinal()] = newMode;
        recomputeCaches();
        setChangedAndSync();

        PipeNetworkManager manager = PipeNetworkManager.get(level);
        if (old == PipeConnection.PIPE || newMode == PipeConnection.PIPE) {
            manager.invalidateTopology(level, worldPosition, type);
        } else {
            manager.updateEndpoint(level, worldPosition, type, dir, newMode);
        }
    }

    // -----------------------------------------------------------------------
    // Network cache (transient — never persisted, rebuilt lazily)
    // -----------------------------------------------------------------------

    @Nullable
    public PipeNetwork getNetworkRef(PipeType type) {
        return networkRef[type.ordinal()];
    }

    public void setNetworkRef(PipeType type, @Nullable PipeNetwork network) {
        networkRef[type.ordinal()] = network;
    }

    // -----------------------------------------------------------------------
    // Shape / active-endpoint caches
    // -----------------------------------------------------------------------

    private void recomputeCaches() {
        cachedHasAnyActiveEndpoint = hasAnyEndpointConnection(PipeType.ITEM)
                || hasAnyEndpointConnection(PipeType.FLUID)
                || hasAnyEndpointConnection(PipeType.ENERGY);

        VoxelShape shape = Shapes.empty();
        for (PipeType type : PipeType.VALUES) {
            if (!present[type.ordinal()]) continue;
            shape = Shapes.or(shape, laneHubShape(type));
            for (Direction dir : Direction.values()) {
                if (connections[type.ordinal()][dir.ordinal()] == PipeConnection.NONE) continue;
                shape = Shapes.or(shape, laneArmShape(dir, type));
            }
        }
        cachedShape = shape.isEmpty() ? Block.box(6, 6, 6, 10, 10, 10) : shape;
    }

    /** Every clickable region of this pipe — a type's small lane connector cube plus one box per existing connection — for wrench hit-testing. */
    public List<PipeHitPart> getHitParts() {
        List<PipeHitPart> parts = new ArrayList<>();
        for (PipeType type : PipeType.VALUES) {
            if (!present[type.ordinal()]) continue;
            parts.add(new PipeHitPart(type, null, laneHubShape(type)));
            for (Direction dir : Direction.values()) {
                if (connections[type.ordinal()][dir.ordinal()] == PipeConnection.NONE) continue;
                parts.add(new PipeHitPart(type, dir, laneArmShape(dir, type)));
            }
        }
        return parts;
    }

    private VoxelShape laneHubShape(PipeType type) {
        double c = PipeGeometry.laneCenter(type);
        double half = PipeGeometry.HALF;
        return Block.box(c - half, c - half, c - half, c + half, c + half, c + half);
    }

    /**
     * Starts exactly at the connector cube's edge and never overlaps it — matching
     * {@code PipeBakedModel}'s arm geometry exactly, so a wrench click resolves to the arm only
     * when it's actually looking at the arm, not wherever an overlapping hub volume used to win by
     * being checked first.
     */
    private VoxelShape laneArmShape(Direction dir, PipeType type) {
        double c = PipeGeometry.laneCenter(type);
        double min = c - PipeGeometry.HALF, max = c + PipeGeometry.HALF;
        return switch (dir) {
            case DOWN -> Block.box(min, 0, min, max, min, max);
            case UP -> Block.box(min, max, min, max, 16, max);
            case NORTH -> Block.box(min, min, 0, max, max, min);
            case SOUTH -> Block.box(min, min, max, max, max, 16);
            case WEST -> Block.box(0, min, min, min, max, max);
            case EAST -> Block.box(max, min, min, 16, max, max);
        };
    }

    public VoxelShape getCachedShape() {
        return cachedShape;
    }

    // getOutlineShape() previously padded the real geometry out a little further, meant to make
    // aiming at a thin 2px pipe more forgiving. In practice a 1.5px pad on each side of a 2px-thick
    // box nearly tripled its apparent cross-section, so the look-outline visibly stopped matching
    // the pipe at all — which made aiming *harder*, not easier, since the outline is exactly what a
    // player lines their crosshair up against. It's gone: this now returns the exact geometry, and
    // LogisticsPipeBlock.getShape()/getCollisionShape() both use it, so what you see is what you
    // can click. (Any remaining forgiveness for a near-miss lives entirely in
    // LogisticsPipeBlock#getHitPart's small fallback tolerance once a hit is already registered.)
    public VoxelShape getOutlineShape() {
        return cachedShape;
    }

    /** Whether the block at {@code pos}, approached from {@code dir}, exposes the given pipe type's capability. */
    public static boolean neighborHasCapability(PipeType type, ServerLevel level, BlockPos pos, Direction dir) {
        return switch (type) {
            case ITEM -> level.getCapability(Capabilities.ItemHandler.BLOCK, pos, dir) != null;
            case FLUID -> level.getCapability(Capabilities.FluidHandler.BLOCK, pos, dir) != null;
            case ENERGY -> level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, dir) != null;
        };
    }

    public boolean hasAnyActiveEndpoint() {
        return cachedHasAnyActiveEndpoint;
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // -----------------------------------------------------------------------
    // Model data (renderer input — see PipeUnbakedGeometry)
    // -----------------------------------------------------------------------

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(RENDER_STATE, new PipeRenderState(present, connections)).build();
    }

    // -----------------------------------------------------------------------
    // Capabilities exposed to non-pipe neighbors (registered in ModBusEvents)
    // -----------------------------------------------------------------------

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction dir) {
        if (dir == null || !getConnection(PipeType.ITEM, dir).isEndpoint()) return null;
        return new PipeEndpointItemHandler(this, dir);
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction dir) {
        if (dir == null || !getConnection(PipeType.FLUID, dir).isEndpoint()) return null;
        return new PipeEndpointFluidHandler(this, dir);
    }

    @Nullable
    public IEnergyStorage getEnergyStorage(@Nullable Direction dir) {
        if (dir == null || !getConnection(PipeType.ENERGY, dir).isEndpoint()) return null;
        return new PipeEndpointEnergyStorage(this, dir);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void onLoad() {
        super.onLoad();
        for (PipeType type : PipeType.VALUES) {
            networkRef[type.ordinal()] = null;
        }
        // PipeNetworkManager's endpoint-bearing set is pure in-memory bookkeeping (never saved to
        // disk, and rebuilt from scratch — empty — every time the manager is (re)created for a
        // dimension); without this, PipeNetworkTickHandler finds zero active networks after a
        // world/server restart until a player re-wrenches something to repopulate it, even though
        // the actual IMPORT/EXPORT connections were loaded correctly from NBT above.
        if (level instanceof ServerLevel sl) {
            PipeNetworkManager manager = PipeNetworkManager.get(sl);
            for (PipeType type : PipeType.VALUES) {
                if (hasAnyEndpointConnection(type)) {
                    manager.setEndpointBearing(type, worldPosition, true);
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel sl) {
            for (PipeType type : PipeType.VALUES) {
                PipeNetworkManager.get(sl).setEndpointBearing(type, worldPosition, false);
            }
        }
    }

    // -----------------------------------------------------------------------
    // NBT
    // -----------------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        byte typeMask = 0;
        for (PipeType type : PipeType.VALUES) {
            if (present[type.ordinal()]) typeMask |= (1 << type.ordinal());
            tag.putInt("Conn" + type.ordinal(), packConnections(type));
        }
        tag.putByte("Types", typeMask);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        byte typeMask = tag.getByte("Types");
        for (PipeType type : PipeType.VALUES) {
            present[type.ordinal()] = (typeMask & (1 << type.ordinal())) != 0;
            unpackConnections(type, tag.getInt("Conn" + type.ordinal()));
        }
        recomputeCaches();
    }

    private int packConnections(PipeType type) {
        int packed = 0;
        PipeConnection[] row = connections[type.ordinal()];
        for (int d = 0; d < 6; d++) {
            packed |= (row[d].ordinal() & 0x3) << (d * 2);
        }
        return packed;
    }

    private void unpackConnections(PipeType type, int packed) {
        PipeConnection[] row = connections[type.ordinal()];
        for (int d = 0; d < 6; d++) {
            row[d] = PipeConnection.byOrdinal((packed >> (d * 2)) & 0x3);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
        recomputeCaches();
        requestModelDataUpdate();
        if (level != null && level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
