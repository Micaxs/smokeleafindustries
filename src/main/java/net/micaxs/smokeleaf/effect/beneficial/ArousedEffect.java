package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ArousedEffect extends MobEffect {
    public ArousedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof Player player) {
                player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(10))
                        .forEach(animal -> animal.getNavigation().moveTo(player, 1.2));
                player.level().getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(10))
                        .forEach(villager -> villager.getNavigation().moveTo(player, 1.2));
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
