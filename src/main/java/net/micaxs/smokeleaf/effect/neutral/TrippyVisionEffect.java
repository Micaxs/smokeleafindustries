package net.micaxs.smokeleaf.effect.neutral;

import net.minecraft.server.level.ServerLevel;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class TrippyVisionEffect extends MobEffect {
    public TrippyVisionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

//    @Override
//    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
//        if (!livingEntity.level().isClientSide()) {
//            MobEffectInstance self = livingEntity.getEffect(ModEffects.TRIPPY_VISION);
//            if (self != null) {
//                int duration = self.getDuration();
//                livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, amplifier, true, false, false));
//            }
//        }
//        return super.applyEffectTick(serverLevel, livingEntity, amplifier);
//    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
