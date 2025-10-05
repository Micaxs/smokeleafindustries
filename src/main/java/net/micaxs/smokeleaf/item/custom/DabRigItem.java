package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.item.ModItems;
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

import java.util.ArrayList;
import java.util.List;

public class DabRigItem extends Item {
    public DabRigItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 30;
    }

    private List<MobEffectInstance> getOffhandEffects(ItemStack offhandItem, LivingEntity livingEntity) {
        List<MobEffectInstance> effects = new ArrayList<>();
        if (!(offhandItem.getItem() instanceof BaseWeedItem weedItem)) {
            return effects;
        }

        Holder<MobEffect> effectHolder = toHolder(weedItem.getEffect(), livingEntity);

        int baseDuration = weedItem.getDuration();
        int amplifier = weedItem.getEffectAmplifier();

        int totalDuration = baseDuration;
        if (livingEntity.hasEffect(effectHolder)) {
            MobEffectInstance current = livingEntity.getEffect(effectHolder);
            if (current != null) {
                totalDuration = current.getDuration() + baseDuration;
            }
        }

        effects.add(new MobEffectInstance(effectHolder, totalDuration, amplifier));
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
            spawnSmokeParticles(level, livingEntity);
            offhandItem.shrink(1);

            // Apply the offhand item's effects
            for (MobEffectInstance effect : offhandEffects) {
                livingEntity.addEffect(effect);
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
            return offhand.is(ModTags.WEED_EXTRACTS) || offhand.is(ModItems.HASH_OIL_TINCTURE.get());
        }
        return false;
    }


    @Override
    public SoundEvent getDrinkingSound() {
        return ModSounds.BONG_HIT.get();
    }

}
