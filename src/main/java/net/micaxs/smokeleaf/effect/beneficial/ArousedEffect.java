package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ArousedEffect extends MobEffect {
    public ArousedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof Player player) {
                player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(10))
                        .forEach(animal -> animal.getNavigation().moveTo(player, 1.2));
                player.level().getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(10))
                        .forEach(villager -> villager.getNavigation().moveTo(player, 1.2));
            }
        }
        return super.applyEffectTick(serverLevel, livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
