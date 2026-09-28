package net.micaxs.smokeleaf.effect.neutral;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class StickyFingersEffect extends MobEffect {

    private static final double PICKUP_RADIUS = 16.0;

    public StickyFingersEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && (entity instanceof Player player)) {

            Level level = entity.level();

            // Pick up nearby items
            List<ItemEntity> items = level.getEntitiesOfClass(
                    ItemEntity.class,
                    player.getBoundingBox().inflate(PICKUP_RADIUS),
                    item -> !item.isRemoved() && item.isAlive()
            );

            for (ItemEntity item : items) {
                ItemStack stack = item.getItem();
                boolean added = player.getInventory().add(stack);

                if (added) item.discard();
            }
        }

        return; // Tick logic executed
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Always tick while effect is active
    }
}
