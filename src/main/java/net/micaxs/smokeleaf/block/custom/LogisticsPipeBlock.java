package net.micaxs.smokeleaf.block.custom;

import com.mojang.serialization.MapCodec;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.micaxs.smokeleaf.block.entity.pipe.PipeHitPart;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A single fixed-{@code BlockState} block (no custom properties — see the pipe system plan for why)
 * that can hold up to one {@link PipeBlockEntity} segment of each {@link PipeType}. All interesting
 * interaction (connecting/disconnecting, import/export) is done by {@code PipeWrenchItem}; a bare
 * right-click on the block itself does nothing. Geometry is entirely driven by a custom dynamic
 * baked model reading {@code ModelData} off the block entity — no ticker is needed on this block at
 * all, since all transfer logic is centrally driven by {@code PipeNetworkTickHandler}.
 *
 * <p>Named {@code LogisticsPipeBlock} rather than {@code PipeBlock} because vanilla already has a
 * {@code net.minecraft.world.level.block.PipeBlock} (the fence/wall connecting-shape base class),
 * which caused an unresolvable import ambiguity under this package's existing wildcard imports.
 */
public class LogisticsPipeBlock extends BaseEntityBlock {

    public LogisticsPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Used for player look-targeting (what a right-click actually hits) and the outline box —
        // deliberately padded a little past the real geometry so aiming at a thin 2px pipe isn't
        // unreasonably fiddly. Real physics collision below stays tight.
        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) {
            return pipe.getOutlineShape();
        }
        return Block.box(6, 6, 6, 10, 10, 10);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) {
            return pipe.getCachedShape();
        }
        return Block.box(6, 6, 6, 10, 10, 10);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Intentionally no ticker: all transfer logic is driven centrally by PipeNetworkTickHandler,
        // which only ever touches endpoint-bearing pipes — registering a per-pipe ticker here would
        // just add dead per-block-entity tick overhead for every passive pipe in the world.
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) {
            for (PipeType type : PipeType.VALUES) {
                if (pipe.hasType(type)) {
                    Block.popResource(level, pos, new ItemStack(itemFor(type)));
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static Item itemFor(PipeType type) {
        return switch (type) {
            case ITEM -> ModItems.ITEM_PIPE.get();
            case FLUID -> ModItems.FLUID_PIPE.get();
            case ENERGY -> ModItems.ENERGY_PIPE.get();
        };
    }

    /**
     * Resolves which single clickable part (a type's lane connector, or one of its existing
     * connector arms) a raycast actually landed on, so the wrench can act on exactly the part the
     * player is looking at instead of needing a separately-selected "mode" — inspired by Modern
     * Industrialization's {@code PipeBlock#getHitPart}/{@code isPartHit} (MIT licensed).
     *
     * <p>Tries exact containment first (nudged toward each box's center to break exact-boundary
     * ties, as MI does) — this is unambiguous even when a much larger *connected* arm of another
     * type sits right next to a small *unconnected* hub, since different types' parts never
     * overlap. Falling back to plain nearest-part distance for every hit (an earlier version of
     * this method) let whichever part had the biggest box — typically an already-connected arm —
     * win any click aimed at a neighboring type's still-tiny, unconnected hub, making it impossible
     * to wrench a second type onto a face another type already occupied.
     *
     * <p>Only when the point lands inside no part at all (a near-miss against {@link #getShape}'s
     * padding — see there for why that padding exists) does it fall back to the closest part
     * within {@link #FALLBACK_MAX_DIST}, so an imprecise-but-close click still resolves sensibly
     * instead of doing nothing.
     */
    @Nullable
    public static PipeHitPart getHitPart(PipeBlockEntity pipe, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        Vec3 posInBlock = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        List<PipeHitPart> parts = pipe.getHitParts();

        for (PipeHitPart part : parts) {
            for (AABB box : part.shape().toAabbs()) {
                Vec3 nudge = box.getCenter().subtract(posInBlock).normalize().scale(1e-4);
                if (box.contains(posInBlock.add(nudge))) {
                    return part;
                }
            }
        }

        PipeHitPart closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (PipeHitPart part : parts) {
            for (AABB box : part.shape().toAabbs()) {
                double distSq = distanceSqToBox(posInBlock, box);
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closest = part;
                }
            }
        }
        return closestDistSq <= FALLBACK_MAX_DIST * FALLBACK_MAX_DIST ? closest : null;
    }

    /** How far (in block fractions) a near-miss may fall outside every part's exact box and still resolve to the closest one — matches {@link PipeBlockEntity#getOutlineShape}'s padding. */
    private static final double FALLBACK_MAX_DIST = 1.5 / 16.0;

    private static double distanceSqToBox(Vec3 p, AABB box) {
        double dx = Math.max(box.minX - p.x, Math.max(0, p.x - box.maxX));
        double dy = Math.max(box.minY - p.y, Math.max(0, p.y - box.maxY));
        double dz = Math.max(box.minZ - p.z, Math.max(0, p.z - box.maxZ));
        return dx * dx + dy * dy + dz * dz;
    }
}
