package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Grants creative-style flight while active. Forge 1.20.1 has no creative-flight attribute, so the
 * effect keeps {@code mayfly} set every tick; CommonEvents revokes it when the effect ends.
 */
public class HighFlyerEffect extends MobEffect {
    public HighFlyerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer sp && !sp.getAbilities().mayfly) {
            sp.getAbilities().mayfly = true;
            sp.onUpdateAbilities();
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
