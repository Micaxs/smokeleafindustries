package net.micaxs.smokeleaf.block.entity.pipe;

/**
 * Shared geometry constants for the pipe model, ported (numbers only — the renderer is built on
 * NeoForge's {@code BlockElement}/{@code VoxelShape} primitives, not MI's Fabric mesh API) from
 * Modern Industrialization's pipe part layout (MIT licensed —
 * https://github.com/AztechMC/Modern-Industrialization, see
 * {@code pipes/impl/PipePartBuilder.java}): every pipe is a thin 2px-square tube instead of a
 * variable-thickness nested cube, and up to 3 coexisting types occupy one of 3 parallel diagonal
 * "lanes" through the block rather than concentric shells, so nothing is ever fully hidden inside
 * another type's geometry and every type remains independently clickable.
 *
 * <p>Unlike MI (which assigns lanes by each position's own dense present-type order — the same
 * type can land in a different lane at two adjacent blocks depending on what else happens to be
 * present at each one), each {@link PipeType} here always renders in the exact same lane
 * everywhere. A continuous same-type run must stay at one fixed cross-section through every block
 * it passes, or it visibly zigzags sideways the moment the set of *other* types present changes
 * from block to block (e.g. right where a second type joins in at a junction).
 *
 * <p>Used identically by {@code PipeBlockEntity} (collision shape + wrench hit-testing) and
 * {@code PipeBakedModel} (render quads), so the two can never drift out of sync with each other.
 */
public final class PipeGeometry {
    private PipeGeometry() {}

    /** Half-thickness of a pipe's cross-section, in pixels (out of 16) — a 2px-wide square tube, matching MI's SIDE constant. */
    public static final double HALF = 1.0;

    // Indexed by PipeType.ordinal(): ITEM, FLUID, ENERGY.
    private static final double[] LANE_CENTER = {5.0, 8.0, 11.0};

    /** The pixel coordinate (shared by all 3 axes) this type's lane is fixed at. */
    public static double laneCenter(PipeType type) {
        return LANE_CENTER[type.ordinal()];
    }
}
