package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainEffectsUtil;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class WeedDerivedItem extends Item {
    private final float effectDurationMultiplier;
    private final float stonedChance;
    private final UseAnim useAnimation;
    private final int useDuration;
    private static final List<ResourceLocation> ADDITIONAL_EFFECT_POOL = Collections.emptyList();

    public WeedDerivedItem(Properties pProperties, float effectDurationMultiplier, float stonedChance, UseAnim useAnimation) {
        this(pProperties, effectDurationMultiplier, stonedChance, useAnimation, 20);
    }

    public WeedDerivedItem(Properties pProperties, float effectDurationMultiplier, float stonedChance, UseAnim useAnimation, int useDuration) {
        super(pProperties);
        this.effectDurationMultiplier = effectDurationMultiplier;
        this.stonedChance = stonedChance;
        this.useAnimation = useAnimation;
        this.useDuration = useDuration;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return this.useAnimation;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.useDuration;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand == InteractionHand.MAIN_HAND) {
            ItemStack itemstack = player.getItemInHand(usedHand);
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        spawnSmokeParticles(level, livingEntity);

        if (!level.isClientSide) {
            StrainData strain = StrainUtil.getStrain(stack);
            if (strain != StrainData.EMPTY) {
                List<MobEffectInstance> effects = StrainEffectsUtil.buildEffectInstances(
                        strain.thc(), strain.cbd(), 0,
                        getBaseEffect(strain),
                        this.effectDurationMultiplier,
                        ADDITIONAL_EFFECT_POOL
                );
                for (MobEffectInstance inst : effects) {
                    if (inst != null && inst.getEffect() != null) {
                        livingEntity.addEffect(inst);
                    }
                }
            }

            if (level.random.nextDouble() <= this.stonedChance) {
                Holder<MobEffect> stonedHolder = resolveModEffectHolder(level);
                int previousStonedDuration = 0;
                if (livingEntity.hasEffect(stonedHolder)) {
                    previousStonedDuration = livingEntity.getEffect(stonedHolder).getDuration();
                }
                livingEntity.addEffect(new MobEffectInstance(stonedHolder, previousStonedDuration + 200, 1));
            }
        }

        stack.shrink(1);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    private MobEffect getBaseEffect(StrainData strain) {
        if (!strain.effects().isEmpty()) {
            ResourceLocation id = strain.effects().get(0);
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect != null) return effect;
        }
        return null;
    }

    private static Holder<MobEffect> resolveModEffectHolder(Level level) {
        Holder<?> h = ModEffects.STONED;
        if (h.value() instanceof MobEffect) {
            @SuppressWarnings("unchecked")
            Holder<MobEffect> cast = (Holder<MobEffect>) h;
            return cast;
        }
        MobEffect effect = ModEffects.STONED.value();
        return mobEffectToHolder(effect, level);
    }

    private static Holder<MobEffect> mobEffectToHolder(MobEffect effect, Level level) {
        return BuiltInRegistries.MOB_EFFECT
                .getResourceKey(effect)
                .flatMap(key -> level.registryAccess()
                        .registryOrThrow(Registries.MOB_EFFECT)
                        .getHolder(key))
                .orElseThrow(() -> new IllegalStateException("Unregistered MobEffect: " + effect));
    }

    private void spawnSmokeParticles(Level level, LivingEntity entity) {
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        StrainData strain = StrainUtil.getStrain(stack);
        if (strain == StrainData.EMPTY) return;

        tooltipComponents.add(
                Component.literal("Levels: ")
                        .append(Component.literal(strain.thc() + "%").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" THC").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(" & ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(strain.cbd() + "%").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" CBD").withStyle(ChatFormatting.DARK_GRAY))
        );
    }

    public float getEffectFactor() {
        return this.effectDurationMultiplier;
    }
}
