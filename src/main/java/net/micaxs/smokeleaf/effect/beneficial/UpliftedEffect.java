package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class UpliftedEffect extends MobEffect {
    public UpliftedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof Player player) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x * 0.05, motion.y, motion.z * 1.05);
                // JUMP Boost added in event
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
