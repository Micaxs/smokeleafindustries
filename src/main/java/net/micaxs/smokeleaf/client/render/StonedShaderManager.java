package net.micaxs.smokeleaf.client.render;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.TripTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Loads/unloads one of {@link TripTier}'s 8 GLSL post-process shaders based on the amplifier of
 * the player's {@link ModEffects#STONED} effect (each amplifier value 0-7 selects a tier — see
 * {@link TripTier#forThc(int)} for how THC picks the amplifier when STONED is granted).
 *
 * <p>Only one {@code PostChain} can be loaded at a time, so this tracks which tier is currently
 * active and swaps chains whenever the amplifier changes (e.g. smoking something stronger mid-trip).
 * The {@code Intensity} uniform fades the effect out over the last 3 seconds of the STONED
 * duration; {@code Time} drives each shader's animation (kaleidoscope spin, colour cycling, etc).
 */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public final class StonedShaderManager {
    private StonedShaderManager() {}

    /** Fade-out window: last 60 ticks (3 seconds) of the effect. */
    private static final int FADE_OUT_TICKS = 60;
    private static final float MIN_INTENSITY = 0.45f;
    private static final float MAX_INTENSITY = 0.95f;

    private static TripTier activeTier = null;
    private static float shaderTime = 0.0f;

    private static float computeIntensity(Minecraft mc, MobEffectInstance stoned) {
        if (stoned == null) return 0.0f;

        float pulse = 0.5f + 0.5f * (float) Math.sin((mc.level.getGameTime() * 0.12f));
        float base = MIN_INTENSITY + (MAX_INTENSITY - MIN_INTENSITY) * pulse;

        if (stoned.getDuration() <= FADE_OUT_TICKS) {
            float fade = stoned.getDuration() / (float) FADE_OUT_TICKS;
            return base * fade;
        }

        return base;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            if (activeTier != null) {
                mc.gameRenderer.shutdownEffect();
                activeTier = null;
            }
            return;
        }

        MobEffectInstance stoned = mc.player.getEffect(ModEffects.STONED);

        // "visible" doubles as the trip-streak gate (see TripStreakTracker) — the shader only
        // shows once the player has used the same item 3 times in a row.
        if (stoned == null || !stoned.isVisible()) {
            if (activeTier != null) {
                mc.gameRenderer.shutdownEffect();
                activeTier = null;
            }
            return;
        }

        TripTier tier = TripTier.byAmplifier(stoned.getAmplifier());
        if (tier != activeTier) {
            if (activeTier != null) mc.gameRenderer.shutdownEffect();
            mc.gameRenderer.loadEffect(tier.shaderChain);
            activeTier = tier;
        }

        shaderTime += 0.05f;

        PostChain chain = mc.gameRenderer.currentEffect();
        if (chain != null) {
            chain.setUniform("Intensity", computeIntensity(mc, stoned));
            chain.setUniform("Time", shaderTime);
        }
    }
}
