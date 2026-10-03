package net.micaxs.smokeleaf.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Overlays small red patches on the player model's eyes (matching the vanilla skin's eye pixel
 * positions on the head's front face) whenever {@link ModEffects#STONED} is active — the classic
 * "red eyes" look, visible to everyone nearby (and to the player themselves in third person),
 * not just a screen tint on the affected player's own view.
 */
public class RedEyesLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/entity/red_eyes.png");

    public RedEyesLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                        float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                        float netHeadYaw, float headPitch) {
        if (player.isInvisible() || !player.hasEffect(ModEffects.STONED)) return;

        var vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0f);
        getParentModel().getHead().render(poseStack, vertexConsumer, packedLight, overlay);
    }
}
