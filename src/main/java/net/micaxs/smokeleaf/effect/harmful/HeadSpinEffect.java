package net.micaxs.smokeleaf.effect.harmful;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class HeadSpinEffect extends MobEffect {
    public HeadSpinEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            MobEffectInstance self = livingEntity.getEffect(ModEffects.DIZZY.get());
            if (self != null) {
                int duration = self.getDuration();
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, amplifier, true, false, false));
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
        return;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}
