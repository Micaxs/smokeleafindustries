package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BongItem extends Item {
    public BongItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 30;
    }


    private List<MobEffectInstance> getOffhandEffects(ItemStack offhandItem, LivingEntity entity) {
        List<MobEffectInstance> effects = new ArrayList<>();
        if (!(offhandItem.getItem() instanceof BaseWeedItem weedItem)) {
            return effects;
        }

        // Build the proper \[base + extra\] effects with CBD-based unified duration
        List<MobEffectInstance> built = weedItem.buildEffectInstances(offhandItem);
        if (built.isEmpty()) return effects;

        // Optional: stack with current effects (add durations)
        for (MobEffectInstance inst : built) {
            var holder = inst.getEffect();                   // Holder<MobEffect>
            int duration = inst.getDuration();
            MobEffectInstance current = entity.getEffect(holder);
            int totalDuration = current != null ? current.getDuration() + duration : duration;

            effects.add(new MobEffectInstance(
                    holder,
                    totalDuration,
                    inst.getAmplifier(),
                    inst.isAmbient(),
                    inst.isVisible(),
                    inst.showIcon()
            ));
        }
        return effects;
    }

    private Holder<MobEffect> toHolder(MobEffect effect, LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT
                .getResourceKey(effect)
                .flatMap(key -> entity.level()
                        .registryAccess()
                        .registryOrThrow(Registries.MOB_EFFECT)
                        .getHolder(key))
                .orElseThrow(() -> new IllegalStateException("Unregistered MobEffect: " + effect));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);

        // Check if the offhand item is valid
        if (!isValidOffhandItem(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return InteractionResultHolder.fail(itemstack);
        }

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        InteractionHand offHand = InteractionHand.OFF_HAND;
        ItemStack offhandItem = livingEntity.getItemInHand(offHand);

        if (isValidOffhandItem(offhandItem)) {
            livingEntity.setItemInHand(InteractionHand.OFF_HAND, offhandItem);

            List<MobEffectInstance> offhandEffects = getOffhandEffects(offhandItem, livingEntity);
            net.micaxs.smokeleaf.strain.StrainData offhandStrain = net.micaxs.smokeleaf.strain.StrainUtil.getStrain(offhandItem);
            spawnSmokeParticles(level, livingEntity);
            offhandItem.shrink(1);

            // Apply the offhand item's effects
            for (MobEffectInstance effect : offhandEffects) {
                livingEntity.addEffect(effect);
            }

            // Grant STONED for the same duration as the longest weed effect. The trip shader
            // tier is picked from the offhand item's THC; high CBD (27-30) shortens the duration.
            // The trip only actually shows once the same item has been smoked 3 times in a row
            // (see TripStreakTracker) — it still grants STONED either way.
            int maxDuration = offhandEffects.stream().mapToInt(MobEffectInstance::getDuration).max().orElse(0);
            if (maxDuration > 0) {
                MobEffectInstance existing = livingEntity.getEffect(ModEffects.STONED);
                int tier = net.micaxs.smokeleaf.effect.TripTier.forThc(
                        offhandStrain != net.micaxs.smokeleaf.strain.StrainData.EMPTY ? offhandStrain.thc() : 0).ordinal();
                float cbdMult = net.micaxs.smokeleaf.effect.TripTier.cbdDurationMultiplier(
                        offhandStrain != net.micaxs.smokeleaf.strain.StrainData.EMPTY ? offhandStrain.cbd() : 0);
                int adjustedDuration = Math.max(1, Math.round(maxDuration * cbdMult));
                int stonedDuration = existing != null ? existing.getDuration() + adjustedDuration : adjustedDuration;
                boolean confirmed = true;
                if (livingEntity instanceof net.minecraft.world.entity.player.Player p) {
                    String streakKey = net.micaxs.smokeleaf.effect.TripStreakTracker.keyFor(offhandItem, offhandStrain);
                    confirmed = net.micaxs.smokeleaf.effect.TripStreakTracker.registerUseAndGetStreak(p, streakKey)
                            >= net.micaxs.smokeleaf.effect.TripStreakTracker.REQUIRED_STREAK;
                }
                livingEntity.addEffect(new MobEffectInstance(ModEffects.STONED, stonedDuration, tier, false, confirmed));
            }
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }


    private void spawnSmokeParticles(Level level, LivingEntity entity) {
        // Adjust particle spawning based on your preferences
        for (int i = 0; i < 10; i++) {
            double xOffset = level.random.nextGaussian() * 0.02D;
            double yOffset = level.random.nextGaussian() * 0.02D;
            double zOffset = level.random.nextGaussian() * 0.02D;

            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    entity.getX() + entity.getBbWidth() * (level.random.nextDouble() - 0.5D),
                    entity.getEyeY(),
                    entity.getZ() + entity.getBbWidth() * (level.random.nextDouble() - 0.5D),
                    xOffset, yOffset, zOffset);
        }
    }


    private boolean isValidOffhandItem(ItemStack offhand) {
        if (!offhand.isEmpty()) {
            return offhand.is(ModTags.WEEDS);
        }
        return false;
    }


    @Override
    public SoundEvent getDrinkingSound() {
        return ModSounds.BONG_HIT.get();
    }
}
