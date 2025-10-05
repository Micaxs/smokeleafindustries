package net.micaxs.smokeleaf.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class StonedEffect extends MobEffect {
    protected StonedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            MobEffectInstance self = livingEntity.getEffect(ModEffects.STONED);
            if (self != null) {
                int duration = self.getDuration();
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, amplifier, true, true, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, amplifier, true, true, false));
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
