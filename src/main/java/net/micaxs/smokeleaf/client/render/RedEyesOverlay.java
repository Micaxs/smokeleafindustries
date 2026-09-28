package net.micaxs.smokeleaf.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderGuiEvent;
@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public final class RedEyesOverlay {
    private RedEyesOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        MobEffectInstance inst = mc.player.getEffect(ModEffects.DRY_EYES.get());
        if (inst == null) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        float alpha = Mth.clamp(0.28f + 0.14f * (inst.getAmplifier() + 1), 0.0f, 0.85f);
        int a = ((int)(alpha * 255.0f)) << 24;
        int colorARGB = a | 0x00a83232;

        GuiGraphics g = event.getGuiGraphics();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Push high Z to ensure on top of everything (screens, tooltips, chat, etc.)
        g.pose().pushPose();
        g.pose().translate(0, 0, 1000); // large Z to guarantee top layer
        g.fill(0, 0, w, h, colorARGB);
        g.pose().popPose();

        RenderSystem.disableBlend();
    }
}
