package net.micaxs.smokeleaf.client;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Game-bus client events: HUD wiggle (Melted), rainbow tint, FOV/camera sway.
 */
@Mod.EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    private static final Set<ResourceLocation> WIGGLED = new HashSet<>();

    private record WiggleSpec(double sx, double ax, double axLvl, boolean ix,
                              double sy, double ay, double ayLvl, boolean iy) {}

    private static final Map<ResourceLocation, WiggleSpec> WIGGLE_SPECS = new HashMap<>();
    static {
        WIGGLE_SPECS.put(VanillaGuiOverlay.HOTBAR.id(),             new WiggleSpec(0.10, 3.0, 1.5, false, 0.18, 1.4, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiOverlay.PLAYER_HEALTH.id(),      new WiggleSpec(0.13, 3.0, 1.5, true,  0.22, 1.8, 0.7, false));
        WIGGLE_SPECS.put(VanillaGuiOverlay.FOOD_LEVEL.id(),         new WiggleSpec(0.17, 2.5, 1.2, false, 0.25, 1.2, 0.5, true));
        WIGGLE_SPECS.put(VanillaGuiOverlay.CHAT_PANEL.id(),               new WiggleSpec(0.21, 4.0, 1.0, false, 0.07, 6.0, 1.5, false));
        WIGGLE_SPECS.put(VanillaGuiOverlay.PLAYER_LIST.id(),           new WiggleSpec(0.15, 5.0, 2.0, true,  0.11, 3.5, 1.2, true));
        WIGGLE_SPECS.put(VanillaGuiOverlay.CROSSHAIR.id(),          new WiggleSpec(0.11, 2.0, 1.0, false, 0.19, 2.2, 0.8, false));
        WIGGLE_SPECS.put(VanillaGuiOverlay.POTION_ICONS.id(),            new WiggleSpec(0.19, 2.0, 1.0, false, 0.16, 1.6, 0.6, false));
        WIGGLE_SPECS.put(VanillaGuiOverlay.EXPERIENCE_BAR.id(),     new WiggleSpec(0.19, 2.0, 1.0, false, 0.27, 1.0, 0.4, true));
        WIGGLE_SPECS.put(VanillaGuiOverlay.ITEM_NAME.id(), new WiggleSpec(0.08, 2.0, 1.0, false, 0.24, 1.3, 0.5, true));
    }


    private static boolean hasMelted(Player p) {
        return p != null && p.getEffect(ModEffects.MELTED.get()) != null;
    }

    private static float partial(RenderGuiOverlayEvent event) {
        return event.getPartialTick();
    }

    private static double partial(ViewportEvent.ComputeFov event) {
        return event.getPartialTick();
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !hasMelted(player)) return;

        ResourceLocation id = event.getOverlay().id();
        WiggleSpec spec = WIGGLE_SPECS.get(id);
        if (spec == null) return;

        MobEffectInstance melted = player.getEffect(ModEffects.MELTED.get());
        int ampLvl = melted != null ? melted.getAmplifier() : 0;

        double t = player.tickCount + partial(event);

        double phaseX = Math.sin(t * spec.sx());
        double phaseY = Math.sin(t * spec.sy() + Math.PI / 2.0); // phase shift to decorrelate
        double xAmp = spec.ax() + ampLvl * spec.axLvl();
        double yAmp = spec.ay() + ampLvl * spec.ayLvl();

        double xOffset = phaseX * xAmp * (spec.ix() ? -1 : 1);
        double yOffset = phaseY * yAmp * (spec.iy() ? -1 : 1);

        GuiGraphics g = event.getGuiGraphics();
        g.pose().pushPose();
        g.pose().translate(xOffset, yOffset, 0);
        WIGGLED.add(id);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPost(RenderGuiOverlayEvent.Post event) {
        if (!WIGGLED.remove(event.getOverlay().id())) return;
        event.getGuiGraphics().pose().popPose();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.RAINBOW.get())) return;

        GuiGraphics gg = event.getGuiGraphics();
        float t = (System.currentTimeMillis() % 5000L) / 5000F;
        int rgb = Color.HSBtoRGB(t, 1F, 1F);
        int color = (80 << 24) | (rgb & 0xFFFFFF);
        gg.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), color);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        double p = partial(event);
        MobEffectInstance breathing = player.getEffect(ModEffects.BREATHING.get());
        MobEffectInstance bubbled = player.getEffect(ModEffects.BUBBLED.get());

        if (breathing != null) {
            float time = (float)(player.tickCount + p);
            int amp = breathing.getAmplifier();
            float amplitude = 0.015f + amp * 0.010f;
            float speed = 0.15f + amp * 0.05f;
            float wave = (float)Math.sin(time * speed) * amplitude;
            event.setFOV(event.getFOV() * (1.0 + wave));
        }

        if (bubbled != null) {
            int amp = bubbled.getAmplifier();
            double boost = -0.50 + amp * 0.12;
            double modified = event.getFOV() * (1.0 + boost);
            event.setFOV(Math.min(170.0, modified));
        }

        // Stoned: very slow, deep FOV pulse — like a long relaxed inhale (~4.8s cycle).
        // Gated the same way the trip shader is: only once the streak requirement is met.
        MobEffectInstance stoned = player.getEffect(ModEffects.STONED.get());
        if (stoned != null && stoned.isVisible()) {
            float time = (float)(player.tickCount + p);
            int amp = stoned.getAmplifier();
            float amplitude = 0.009f + amp * 0.004f;
            float wave = (float)Math.sin(time * 0.065f) * amplitude;
            event.setFOV(event.getFOV() * (1.0 + wave));
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        MobEffectInstance stoned = player.getEffect(ModEffects.STONED.get());
        if (stoned != null && stoned.isVisible()) {
            int amp = stoned.getAmplifier();
            float time = (float)(player.tickCount + event.getPartialTick());
            // Two slow sines with different periods — feels organic, not mechanical
            float sway = (float)Math.sin(time * 0.020f) * (0.40f + amp * 0.15f);
            float drift = (float)Math.sin(time * 0.013f + 1.8f) * (0.20f + amp * 0.08f);
            event.setRoll(event.getRoll() + sway + drift);
        }
    }
}
