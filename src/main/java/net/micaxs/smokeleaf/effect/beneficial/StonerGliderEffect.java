package net.micaxs.smokeleaf.effect.beneficial;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class StonerGliderEffect extends MobEffect {
    public StonerGliderEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

//    @Override
//    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
//            if (!livingEntity.level().isClientSide()) {
//                MobEffectInstance self = livingEntity.getEffect(ModEffects.STONER_GLIDE.get());
//                if (self != null) {
//                    int duration = self.getDuration();
//                    livingEntity.addEffect(new MobEffectInstance(
//                            MobEffects.SLOW_FALLING,
//                            duration,
//                            0,
//                            true,
//                            false,
//                            false
//                    ));
//
//                    if (livingEntity.hasEffect(MobEffects.SLOW_FALLING)) {
//                        Vec3 motion = livingEntity.getDeltaMovement();
//                        if (motion.y < 0.0D) {
//                            Vec3 look = livingEntity.getLookAngle();
//                            Vec3 horizontalDir = new Vec3(look.x, 0.0D, look.z);
//                            if (horizontalDir.lengthSqr() > 1.0E-4D) {
//                                double forwardSpeed = -motion.y;
//                                Vec3 forward = horizontalDir.normalize().scale(forwardSpeed);
//                                livingEntity.setDeltaMovement(forward.x, motion.y, forward.z);
//                            }
//                        }
//                    }
//                }
//            }
//            return true;
//    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}
