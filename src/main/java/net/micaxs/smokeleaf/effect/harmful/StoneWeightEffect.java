package net.micaxs.smokeleaf.effect.harmful;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class StoneWeightEffect extends MobEffect {
    public StoneWeightEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

//    @Override
//    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
//        if (!livingEntity.level().isClientSide()) {
//            MobEffectInstance self = livingEntity.getEffect(ModEffects.STONE_WEIGHT);
//            if (self != null) {
//                int duration = self.getDuration();
//                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, true, false, false));
//            }
//        }
//        return super.applyEffectTick(livingEntity, amplifier);
//    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
