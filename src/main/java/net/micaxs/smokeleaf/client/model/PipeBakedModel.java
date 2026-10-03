package net.micaxs.smokeleaf.client.model;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.micaxs.smokeleaf.block.entity.pipe.PipeConnection;
import net.micaxs.smokeleaf.block.entity.pipe.PipeGeometry;
import net.micaxs.smokeleaf.block.entity.pipe.PipeRenderState;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Builds the pipe's visible geometry — a small connector cube per present type plus one arm per
 * *connected* direction, all sharing the same 2px-thick lane cross-section (see
 * {@link PipeGeometry}, ported from Modern Industrialization's pipe layout, MIT licensed) — fresh
 * from {@link PipeRenderState} on every {@link #getQuads} call. This only runs on a real chunk
 * remesh — triggered by {@code requestModelDataUpdate()} after a connection actually changes —
 * never per frame, so baking BlockElements here on demand instead of pre-baking every permutation
 * is cheap and keeps this class simple.
 *
 * <p>Each arm is built from one or two boxes that start exactly at the connector cube's edge and
 * reach the block face — never overlapping the connector or each other — with only outward-facing
 * side/end faces ever emitted; any face touching the connector cube (or, for a two-part
 * Import/Export arm, the boundary between its two segments) is always skipped as permanently
 * interior. Earlier revisions built each arm as one long box straight through the connector's own
 * volume, which left multiple opaque, unculled faces buried inside each other at every joint — the
 * source of the z-fighting/flicker artifacts at corners and T-junctions. A pipe-to-pipe (PIPE)
 * connection is a single flush segment with no cap at all (the run stays visually "open" all the
 * way through into the next block); an Import/Export connection still reaches flush to the
 * machine's face, but its last {@link #TIP_LENGTH} px is a distinct segment whose 4 side faces
 * *and* far cap all carry the endpoint arrow texture, so the direction reads from the side as you
 * walk past, not only when looking straight down the pipe's own axis. Every face also uses a
 * fixed full-texture UV instead of one derived from the element's own position, so the same
 * casing pattern reads identically on every face regardless of that face's size or where in the
 * block it sits. Box math mirrors {@code PipeBlockEntity#getHitParts()}/{@code recomputeCaches()}
 * so the rendered shape always matches the collision and wrench hit-test shapes exactly (aside
 * from the cosmetic Import/Export tip split, which is render-only).
 */
public class PipeBakedModel implements IDynamicBakedModel {
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final ModelState modelState;
    private final ItemOverrides overrides;
    private final TextureAtlasSprite particleIcon;

    public PipeBakedModel(Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon) {
        this.spriteGetter = spriteGetter;
        this.modelState = modelState;
        this.overrides = overrides;
        this.particleIcon = particleIcon;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        // Every element here is registered as an "unculled" face (no cullForDirection), so all
        // quads live in the null-side bucket; nothing is ever contributed to a specific side.
        if (side != null) return List.of();

        PipeRenderState renderState = extraData.get(PipeBlockEntity.RENDER_STATE);
        if (renderState == null) renderState = PipeRenderState.EMPTY;

        List<BlockElement> elements = buildElements(renderState);
        if (elements.isEmpty()) return List.of();
        return UnbakedGeometryHelper.bakeElements(elements, spriteGetter, modelState);
    }

    /** Fixed full-texture mapping used on every face, regardless of that face's actual size/position — see the class doc. */
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0, 0, 16, 16}, 0);

    private static List<BlockElement> buildElements(PipeRenderState rs) {
        List<BlockElement> elements = new ArrayList<>();
        for (PipeType type : PipeType.VALUES) {
            int typeIdx = type.ordinal();
            if (!rs.present[typeIdx]) continue;

            String bodyTexture = bodyTexture(type);
            double c = PipeGeometry.laneCenter(type);
            double half = PipeGeometry.HALF;
            double min = c - half, max = c + half;

            // Connector cube: skip any face a connection continues through (permanently interior).
            // An open (unconnected) face gets the socket-ring texture instead of the plain body, so
            // it reads at a glance as an available wrench target.
            Map<Direction, BlockElementFace> centerFaces = new EnumMap<>(Direction.class);
            for (Direction dir : Direction.values()) {
                if (rs.connections[typeIdx][dir.ordinal()] == PipeConnection.NONE) {
                    centerFaces.put(dir, new BlockElementFace(null, -1, openTexture(type), FULL_UV));
                }
            }
            elements.add(new BlockElement(
                    new Vector3f((float) min, (float) min, (float) min),
                    new Vector3f((float) max, (float) max, (float) max),
                    centerFaces, null, true));

            for (Direction dir : Direction.values()) {
                PipeConnection conn = rs.connections[typeIdx][dir.ordinal()];
                if (conn == PipeConnection.NONE) continue;
                elements.addAll(armElements(dir, c, half, bodyTexture, conn));
            }
        }
        return elements;
    }

    /** How far (in px) the Import/Export indicator tip extends back from the machine's face. */
    private static final double TIP_LENGTH = 2.5;
    /** A hair's width short of the true block boundary, just enough to dodge z-fighting with the machine's own face while still reading as flush. */
    private static final double FLUSH_EPSILON = 0.1;

    private static List<BlockElement> armElements(Direction dir, double c, double half, String bodyTexture, PipeConnection conn) {
        List<BlockElement> elements = new ArrayList<>();
        double reach = reach(dir, c, half); // distance from the block face to the connector cube's edge

        if (conn == PipeConnection.PIPE) {
            // One segment, flush both ends, no cap either side — a pipe-to-pipe run stays visually
            // open all the way through into the next block's own pipe.
            elements.add(armSegment(dir, c, half, reach, 0, bodyTexture, null, null));
            return elements;
        }

        // Import/Export: the pipe still reaches all the way to the machine (flush, minus a hair to
        // dodge z-fighting) — but the last TIP_LENGTH px is a separate segment whose 4 side faces
        // (and far cap) all carry the endpoint arrow texture, so the direction reads from the side
        // as you walk past, not only when looking straight down the pipe's own axis. Both point
        // toward the machine (dir) — the arrow shows which face is connected, not flow direction,
        // which is instead told apart by color (blue = Import, yellow = Export).
        String tip = endpointTexture(conn);
        Direction apexTarget = dir;
        elements.add(armSegment(dir, c, half, reach, TIP_LENGTH, bodyTexture, null, null));
        elements.add(armSegment(dir, c, half, TIP_LENGTH, FLUSH_EPSILON, tip, tip, apexTarget));
        return elements;
    }

    // Which world Direction a face's texture "apex" (its default V=0 edge, i.e. row 0 of the PNG —
    // where this mod's endpoint textures put the triangle's point) faces at UV rotation 0°, 90°,
    // 180° and 270°, indexed by [faceDirection.ordinal()][rotation/90]. Derived directly from
    // vanilla's FaceBakery/FaceInfo vertex-to-UV mapping (not guessed): without this, a face's
    // default UV orientation has nothing to do with the pipe's own travel direction, which is
    // exactly why the arrow used to look like it was pointing "randomly" from face to face.
    private static final Direction[][] APEX_DIRECTION_BY_ROTATION = {
            {Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST}, // DOWN
            {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, // UP
            {Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST},     // NORTH
            {Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST},     // SOUTH
            {Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH},   // WEST
            {Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH},   // EAST
    };

    /** The UV rotation (0/90/180/270) that makes {@code side}'s texture apex point toward {@code desired}. */
    private static int rotationFor(Direction side, Direction desired) {
        Direction[] row = APEX_DIRECTION_BY_ROTATION[side.ordinal()];
        for (int r = 0; r < 4; r++) {
            if (row[r] == desired) return r * 90;
        }
        throw new IllegalStateException("No rotation of " + side + " points toward " + desired);
    }

    private static double reach(Direction dir, double c, double half) {
        return switch (dir) {
            case DOWN, NORTH, WEST -> c - half;
            case UP, SOUTH, EAST -> 16 - (c + half);
        };
    }

    /**
     * One segment of an arm along its travel axis, from {@code nearDist} px out from the block
     * face down to {@code farDist} px out from it ({@code nearDist > farDist}: "near"/"far" as in
     * distance from the connector cube, so the segment closer to the connector has the larger
     * distance-from-face). The near-end cap is always omitted — it touches either the connector
     * cube or the previous segment, and is therefore always interior; the far-end cap uses
     * {@code capTexture}, or is omitted entirely when null.
     */
    private static BlockElement armSegment(Direction dir, double c, double half, double nearDist, double farDist, String sideTexture, @Nullable String capTexture, @Nullable Direction apexTarget) {
        double min = c - half, max = c + half;
        double x1, y1, z1, x2, y2, z2;
        switch (dir) {
            case DOWN -> { x1 = min; y1 = farDist; z1 = min; x2 = max; y2 = nearDist; z2 = max; }
            case UP -> { x1 = min; y1 = 16 - nearDist; z1 = min; x2 = max; y2 = 16 - farDist; z2 = max; }
            case NORTH -> { x1 = min; y1 = min; z1 = farDist; x2 = max; y2 = max; z2 = nearDist; }
            case SOUTH -> { x1 = min; y1 = min; z1 = 16 - nearDist; x2 = max; y2 = max; z2 = 16 - farDist; }
            case WEST -> { x1 = farDist; y1 = min; z1 = min; x2 = nearDist; y2 = max; z2 = max; }
            default -> { x1 = 16 - nearDist; y1 = min; z1 = min; x2 = 16 - farDist; y2 = max; z2 = max; } // EAST
        }

        Map<Direction, BlockElementFace> faces = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values()) {
            if (side == dir.getOpposite()) continue; // near cap: always interior
            if (side == dir) {
                if (capTexture != null) {
                    faces.put(side, new BlockElementFace(null, -1, capTexture, FULL_UV));
                }
                continue;
            }
            // A directional (arrow) side texture needs its own per-face rotation so the apex
            // consistently points toward apexTarget — the face's default (unrotated) orientation
            // has nothing to do with the pipe's travel direction. A plain, non-directional texture
            // (apexTarget == null) always uses the untouched default mapping.
            BlockFaceUV uv = apexTarget != null ? new BlockFaceUV(new float[]{0, 0, 16, 16}, rotationFor(side, apexTarget)) : FULL_UV;
            faces.put(side, new BlockElementFace(null, -1, sideTexture, uv));
        }
        return new BlockElement(new Vector3f((float) x1, (float) y1, (float) z1), new Vector3f((float) x2, (float) y2, (float) z2), faces, null, true);
    }

    private static String bodyTexture(PipeType type) {
        return switch (type) {
            case ITEM -> SmokeleafIndustries.MODID + ":block/pipe_item";
            case FLUID -> SmokeleafIndustries.MODID + ":block/pipe_fluid";
            case ENERGY -> SmokeleafIndustries.MODID + ":block/pipe_energy";
        };
    }

    /** Marks an unconnected connector face as an available wrench target with a visible socket ring. */
    private static String openTexture(PipeType type) {
        return switch (type) {
            case ITEM -> SmokeleafIndustries.MODID + ":block/pipe_item_open";
            case FLUID -> SmokeleafIndustries.MODID + ":block/pipe_fluid_open";
            case ENERGY -> SmokeleafIndustries.MODID + ":block/pipe_energy_open";
        };
    }

    /**
     * The arrow-marked cap texture for an IMPORT/EXPORT endpoint face — never called for a PIPE
     * connection (its far cap is skipped entirely). Shared across all 3 pipe types and drawn as a
     * bold, high-contrast solid color plate rather than a thin white-on-outline shape: a fine
     * triangle silhouette on a small 2px cap gets mipmapped into an indistinct grey blob at normal
     * viewing distance, indistinguishable from the open-port socket ring, whereas a strong color
     * difference (blue vs. yellow, both far from every pipe body hue) keeps reading correctly even
     * blurred.
     */
    private static String endpointTexture(PipeConnection conn) {
        return SmokeleafIndustries.MODID + ":block/pipe_endpoint_" + (conn == PipeConnection.IMPORT ? "in" : "out");
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleIcon;
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }
}
