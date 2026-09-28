// java
package net.micaxs.smokeleaf.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StickyIckyEffect extends MobEffect {
    // Grace period so freshly dropped items can fall before being re-picked
    private static final int SELF_DROP_DELAY_TICKS = 12; // ~0.6s
    // Only consider items very close to the player as "fresh self-drops"
    private static final double SELF_DROP_DISTANCE_SQR = 9.0D; // 3 blocks

    public StickyIckyEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // tick every tick
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        double radius = 4.0D + (amplifier + 1) * 1.5D;
        AABB range = player.getBoundingBox().inflate(radius);

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, range, it ->
                it.isAlive() && !it.getItem().isEmpty()
        );

        for (ItemEntity item : items) {
            int age = item.getAge();

            // Treat very fresh items near the player as self-drops: give them a short delay and don't attract yet
            if (age < SELF_DROP_DELAY_TICKS && item.distanceToSqr(player) < SELF_DROP_DISTANCE_SQR) {
                int remaining = SELF_DROP_DELAY_TICKS - age;
                if (remaining > 0 && !item.hasPickUpDelay()) {
                    item.setPickUpDelay(remaining);
                }
                continue; // let it fall first
            }

            // Skip any item that currently has a pickup delay (vanilla timing)
            if (item.hasPickUpDelay()) {
                continue;
            }

            // Magnet behavior: pull items toward the player
            Vec3 target = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);
            Vec3 delta = target.subtract(item.position());

            double strength = 0.10D + 0.05D * (amplifier + 1);
            Vec3 pull = delta.normalize().scale(Math.min(delta.length(), 1.0D) * strength);

            item.setDeltaMovement(item.getDeltaMovement().add(pull));
            item.hasImpulse = true;
        }

        return;
    }
}
