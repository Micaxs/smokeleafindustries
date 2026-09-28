package net.micaxs.smokeleaf.effect.neutral;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ZombifiedEffect extends MobEffect {
    public ZombifiedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (level.isClientSide) return;

        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return;

            BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
            boolean inSunlight = level.isDay()
                    && level.canSeeSky(pos)
                    && !player.isInWaterRainOrBubble();

            if (inSunlight) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                if (helmet.isEmpty()) {
                    int fireTicks = 8 * 20;
                    if (player.getRemainingFireTicks() < fireTicks) {
                        player.setRemainingFireTicks(fireTicks);
                    }
                } else {
                    helmet.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.HEAD));
                }
            }
        }
        return;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
