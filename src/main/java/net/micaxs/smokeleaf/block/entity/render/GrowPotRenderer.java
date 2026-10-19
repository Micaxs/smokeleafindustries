package net.micaxs.smokeleaf.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.micaxs.smokeleaf.block.entity.GrowPotBlockEntity;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrowPotRenderer implements BlockEntityRenderer<GrowPotBlockEntity, GrowPotRenderer.State> {

    private static final float XZ_SCALE = 12f / 16f;
    private static final float Y_BASE = 1.0f;
    private static final float POT_HEIGHT = 8f / 16f;
    private static final float SOIL_HEIGHT = 3f / 16f;
    private static final float MAX_TOTAL_HEIGHT = 2.0f;
    private static final float MAX_CROP_HEIGHT = 2.0f;
    private static final float CROP_Y_SCALE = Math.max(0f, (MAX_TOTAL_HEIGHT - POT_HEIGHT - SOIL_HEIGHT) / MAX_CROP_HEIGHT);

    public GrowPotRenderer(BlockEntityRendererProvider.Context ctx) {}

    /** One block model to draw: its parts and the color for each tint index. */
    private record Piece(List<BlockStateModelPart> parts, int[] tints) {}

    public static class State extends BlockEntityRenderState {
        @Nullable Piece soil;
        @Nullable Piece cropBottom;
        @Nullable Piece cropTop;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(GrowPotBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
        state.soil = state.cropBottom = state.cropTop = null;
        if (!(be.getLevel() instanceof ClientLevel level)) return;
        BlockPos pos = be.getBlockPos();

        BlockState soil = be.getSoilState();
        if (soil != null) state.soil = piece(level, pos, soil, null);

        // The two-layer unidentified crop model needs tint index 0 → leaf color and 1 → strain
        // color from the pot's own strain (the crop isn't really placed, so the world tint
        // sources can't find its data).
        StrainData customStrain = be.getCustomStrain();
        int[] customTints = customStrain != null && customStrain != StrainData.EMPTY
                ? new int[]{customStrain.leafColor(), customStrain.colorArgb()}
                : null;

        BlockState cropBottom = be.getBottomCropStateForRender();
        if (cropBottom == null) return;
        state.cropBottom = piece(level, pos, cropBottom, customTints);
        BlockState cropTop = be.getTopCropStateForRender();
        if (cropTop != null) state.cropTop = piece(level, pos, cropTop, customTints);
    }

    private static Piece piece(ClientLevel level, BlockPos pos, BlockState blockState, int @Nullable [] tints) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(level, pos, blockState, RandomSource.create(blockState.getSeed(pos)), parts);
        if (tints == null) {
            List<BlockTintSource> sources = Minecraft.getInstance().getBlockColors().getTintSources(blockState);
            tints = new int[sources.size()];
            for (int i = 0; i < tints.length; i++) {
                tints[i] = sources.get(i).colorInWorld(blockState, level, pos);
            }
        }
        return new Piece(parts, tints);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.soil != null) {
            pose.pushPose();
            pose.translate(0.5D, 0.0D, 0.5D);
            pose.scale(XZ_SCALE, SOIL_HEIGHT, XZ_SCALE);
            pose.translate(-0.5D, 0.0D, -0.5D);
            pose.translate(0.0D, Y_BASE, 0.0D);
            submitPiece(state.soil, pose, collector, state.lightCoords);
            pose.popPose();
        }

        if (state.cropBottom == null) return;

        pose.pushPose();
        pose.translate(0.5D, 0.0D, 0.5D);
        pose.scale(XZ_SCALE, CROP_Y_SCALE, XZ_SCALE);
        pose.translate(-0.5D, 0.0D, -0.5D);
        pose.translate(0.0D, 0.6f, 0.0D);
        submitPiece(state.cropBottom, pose, collector, state.lightCoords);
        if (state.cropTop != null) {
            pose.translate(0.0D, 1.0D, 0.0D);
            submitPiece(state.cropTop, pose, collector, state.lightCoords);
        }
        pose.popPose();
    }

    private static void submitPiece(Piece piece, PoseStack pose, SubmitNodeCollector collector, int light) {
        collector.submitMultiLayerBlockModel(pose, piece.parts(), false, piece.tints(), light, OverlayTexture.NO_OVERLAY, 0);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
