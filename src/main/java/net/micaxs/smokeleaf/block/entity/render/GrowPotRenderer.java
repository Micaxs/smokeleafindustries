// Java
package net.micaxs.smokeleaf.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.micaxs.smokeleaf.block.entity.GrowPotBlockEntity;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;

public class GrowPotRenderer implements BlockEntityRenderer<GrowPotBlockEntity> {
    private final BlockRenderDispatcher dispatcher;

    /** Fallback base-layer tint if strain has no leaf color. */
    private static final int BASE_GREEN = 0xFF99D335;

    private static final float XZ_SCALE = 12f / 16f;
    private static final float Y_BASE = 1.0f;
    private static final float POT_HEIGHT = 8f / 16f;
    private static final float SOIL_HEIGHT = 3f / 16f;
    private static final float MAX_TOTAL_HEIGHT = 2.0f;
    private static final float MAX_CROP_HEIGHT = 2.0f;
    private static final float CROP_Y_SCALE = Math.max(0f, (MAX_TOTAL_HEIGHT - POT_HEIGHT - SOIL_HEIGHT) / MAX_CROP_HEIGHT);

    public GrowPotRenderer(BlockEntityRendererProvider.Context ctx) {
        this.dispatcher = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(GrowPotBlockEntity be, float partialTicks, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Level level = be.getLevel();
        if (level == null) return;

        BlockState soil = be.getSoilState();
        if (soil != null) {
            pose.pushPose();
            pose.translate(0.5D, 0.0D, 0.5D);
            pose.scale(XZ_SCALE, SOIL_HEIGHT, XZ_SCALE);
            pose.translate(-0.5D, 0.0D, -0.5D);
            pose.translate(0.0D, Y_BASE, 0.0D);
            dispatcher.renderSingleBlock(soil, pose, buffers, packedLight, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }

        BlockState cropBottom = be.getBottomCropStateForRender();
        if (cropBottom == null) return;

        StrainData customStrain = be.getCustomStrain();
        boolean isCustom = customStrain != null && customStrain != StrainData.EMPTY;

        pose.pushPose();
        pose.translate(0.5D, 0.0D, 0.5D);
        pose.scale(XZ_SCALE, CROP_Y_SCALE, XZ_SCALE);
        pose.translate(-0.5D, 0.0D, -0.5D);
        pose.translate(0.0D, 0.6f, 0.0D);

        if (isCustom) {
            // renderSingleBlock only queries BlockColors at tintIndex=0 and applies that
            // single color to every quad. For the two-layer unidentified crop model we need
            // tintIndex=0 → leaf color and tintIndex=1 → strain color, so we render the
            // quads ourselves with per-quad tinting.
            int leafColor = customStrain.leafColor();
            renderTintedModel(cropBottom, leafColor, customStrain.colorArgb(), pose.last(), buffers, packedLight, packedOverlay);
            BlockState cropTop = be.getTopCropStateForRender();
            if (cropTop != null) {
                pose.translate(0.0D, 1.0D, 0.0D);
                renderTintedModel(cropTop, leafColor, customStrain.colorArgb(), pose.last(), buffers, packedLight, packedOverlay);
            }
        } else {
            dispatcher.renderSingleBlock(cropBottom, pose, buffers, packedLight, OverlayTexture.NO_OVERLAY);
            BlockState cropTop = be.getTopCropStateForRender();
            if (cropTop != null) {
                pose.translate(0.0D, 1.0D, 0.0D);
                dispatcher.renderSingleBlock(cropTop, pose, buffers, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }

        pose.popPose();
    }

    /**
     * Renders all quads of {@code state}'s model with per-quad tinting:
     * tintIndex 0 → {@code leafColor} (the strain leaf color), tintIndex 1 → {@code maskColor} (the strain accent color).
     * This bypasses {@code renderSingleBlock}, which only supports a single tint color
     * derived from tintIndex 0.
     */
    private void renderTintedModel(BlockState state, int leafColor, int maskColor,
            PoseStack.Pose pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BakedModel model = dispatcher.getBlockModel(state);
        RandomSource random = RandomSource.create();

        for (RenderType renderType : model.getRenderTypes(state, random, ModelData.EMPTY)) {
            VertexConsumer consumer = buffers.getBuffer(renderType);
            // null direction = unculled quads (cross models place all faces here)
            renderQuadList(model.getQuads(state, null, random, ModelData.EMPTY, renderType),
                    consumer, pose, leafColor, maskColor, packedLight, packedOverlay);
            for (Direction dir : Direction.values()) {
                renderQuadList(model.getQuads(state, dir, random, ModelData.EMPTY, renderType),
                        consumer, pose, leafColor, maskColor, packedLight, packedOverlay);
            }
        }
    }

    private static void renderQuadList(List<BakedQuad> quads, VertexConsumer consumer,
            PoseStack.Pose pose, int leafColor, int maskColor, int packedLight, int packedOverlay) {
        for (BakedQuad quad : quads) {
            int color;
            if (quad.isTinted()) {
                color = (quad.getTintIndex() == 1) ? maskColor : leafColor;
            } else {
                color = 0xFFFFFFFF;
            }
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            consumer.putBulkData(pose, quad, r, g, b, 1.0f, packedLight, packedOverlay);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(GrowPotBlockEntity be) {
        return true;
    }
}
