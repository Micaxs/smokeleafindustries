package net.micaxs.smokeleaf.client.render;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.effect.TripTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Small icon + name badge in the top-left corner showing which {@link TripTier} is currently active. */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID, value = Dist.CLIENT)
public final class TripHudOverlay {
    private TripHudOverlay() {}

    private static final int ICON_SIZE = 24;
    private static final int X = 6;
    private static final int Y = 6;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        MobEffectInstance stoned = mc.player.getEffect(ModEffects.STONED);
        if (stoned == null || !stoned.isVisible()) return;

        TripTier tier = TripTier.byAmplifier(stoned.getAmplifier());
        GuiGraphics g = event.getGuiGraphics();

        g.blit(tier.icon, X, Y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        int textX = X + ICON_SIZE + 5;
        int textY = Y + (ICON_SIZE / 2) - 4;
        int color = tier.unplayable ? 0xFFFF5555 : 0xFFAAFFAA;
        g.drawString(mc.font, tier.displayName, textX, textY, color, true);
    }
}
