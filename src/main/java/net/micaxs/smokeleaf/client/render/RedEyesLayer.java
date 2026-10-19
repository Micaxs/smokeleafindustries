package net.micaxs.smokeleaf.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

/**
 * Overlays small red patches on the player model's eyes (matching the vanilla skin's eye pixel
 * positions on the head's front face) whenever {@link ModEffects#STONED} is active — the classic
 * "red eyes" look, visible to everyone nearby (and to the player themselves in third person),
 * not just a screen tint on the affected player's own view.
 *
 * <p>Renderers only see render states now, so whether the player is stoned is copied onto the
 * state as {@link #STONED} render data (registered in {@code SmokeleafIndustriesClient}).
 */
public class RedEyesLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/entity/red_eyes.png");

    public static final ContextKey<Boolean> STONED = new ContextKey<>(Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "stoned"));

    public RedEyesLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState state, float yRot, float xRot) {
        if (state.isInvisible || !Boolean.TRUE.equals(state.getRenderData(STONED))) return;

        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0f);
        collector.submitModelPart(getParentModel().head, poseStack, RenderTypes.entityTranslucent(TEXTURE), packedLight, overlay, null);
    }
}
