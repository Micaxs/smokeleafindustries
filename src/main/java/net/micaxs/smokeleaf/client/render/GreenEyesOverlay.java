//package net.micaxs.smokeleaf.client.render;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.micaxs.smokeleaf.SmokeleafIndustries;
//import net.micaxs.smokeleaf.effect.ModEffects;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.util.Mth;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.client.event.RenderGuiEvent;
//
//@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
//public final class GreenEyesOverlay {
//    private GreenEyesOverlay() {}

//    @SubscribeEvent
//    public static void onRenderGui(RenderGuiEvent.Post event) {
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player == null || mc.level == null) return;
//        if (mc.screen != null) return; // skip when a screen is open
//
//        MobEffectInstance inst = mc.player.getEffect(ModEffects.GREEN_EYES);
//        if (inst == null) return;
//
//        int w = mc.getWindow().getGuiScaledWidth();
//        int h = mc.getWindow().getGuiScaledHeight();
//
//        // Base alpha + per‑amplifier boost, clamped
//        float alpha = Mth.clamp(0.28f + 0.14f * (inst.getAmplifier() + 1), 0.0f, 0.85f);
//        int a = ((int)(alpha * 255.0f)) << 24;
//        int colorARGB = a | 0x0060a832; // ARGB: AA RR GG BB
//
//        GuiGraphics g = event.getGuiGraphics();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        g.fill(0, 0, w, h, colorARGB);
//        RenderSystem.disableBlend();
//    }
//}