package net.micaxs.smokeleaf.client.render;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.TripTier;
import net.minecraft.client.Minecraft;
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
 * <p>Only one post effect can be active at a time, so this tracks which tier is currently
 * active and swaps effects whenever the amplifier changes (e.g. smoking something stronger mid-trip).
 * Post-effect uniforms can't be updated from code since 1.21.5, so each shader derives its own
 * animation clock and intensity pulse from the global game time.
 */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public final class StonedShaderManager {
    private StonedShaderManager() {}

    private static TripTier activeTier = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            clear(mc);
            return;
        }

        MobEffectInstance stoned = mc.player.getEffect(ModEffects.STONED);

        // "visible" doubles as the trip-streak gate (see TripStreakTracker) — the shader only
        // shows once the player has used the same item 3 times in a row.
        if (stoned == null || !stoned.isVisible()) {
            clear(mc);
            return;
        }

        TripTier tier = TripTier.byAmplifier(stoned.getAmplifier());
        if (tier != activeTier || !tier.shaderChain.equals(mc.gameRenderer.currentPostEffect())) {
            mc.gameRenderer.setPostEffect(tier.shaderChain);
            activeTier = tier;
        }
    }

    /** Removes our trip effect, leaving any other active post effect (e.g. spectating a creeper) alone. */
    private static void clear(Minecraft mc) {
        if (activeTier != null) {
            if (activeTier.shaderChain.equals(mc.gameRenderer.currentPostEffect())) {
                mc.gameRenderer.clearPostEffect();
            }
            activeTier = null;
        }
    }
}
