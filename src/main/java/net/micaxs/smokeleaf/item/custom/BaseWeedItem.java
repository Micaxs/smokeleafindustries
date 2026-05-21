package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainEffectsUtil;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Collection;
import java.util.Objects;

/**
 * THC controls extra effects count.
 * CBD controls unified duration for all effects.
 * Selection is deterministic based on item id, base effect id, THC, CBD.
 */
public class BaseWeedItem extends Item {

    // Configurable pool of possible extra effects (exclude the base effect automatically).
    private static final List<ResourceLocation> ADDITIONAL_EFFECT_POOL = new ArrayList<>();

    public static void setAdditionalEffectPool(Collection<ResourceLocation> effectIds) {
        ADDITIONAL_EFFECT_POOL.clear();
        for (ResourceLocation rl : effectIds) {
            // Only keep effect ids that actually exist
            if (rl != null && BuiltInRegistries.MOB_EFFECT.containsKey(rl)) {
                ADDITIONAL_EFFECT_POOL.add(rl);
            }
        }
    }

    public static void addToAdditionalEffectPool(ResourceLocation effectId) {
        if (effectId != null && BuiltInRegistries.MOB_EFFECT.containsKey(effectId)) {
            ADDITIONAL_EFFECT_POOL.add(effectId);
        }
    }

    private final MobEffect effect; // always applied
    private final int duration;     // legacy/fallback base duration (unused after CBD override)
    private int effectAmplifier;
    private final int thcLevel;
    private final int cbdLevel;
    private boolean variableDuration;
    private final String[] weedNameParts = new String[2];
    private float durationMultiplier = 1;
    private String nameSuffix = " Weed";

    public BaseWeedItem(Properties pProperties, MobEffect effect, int iDuration, int iAmplifier, int iThc, int iCbd, String weedNamePart1, String weedNamePart2) {
        this(pProperties, effect, iDuration, iAmplifier, iThc, iCbd, true);
        this.weedNameParts[0] = weedNamePart1;
        this.weedNameParts[1] = weedNamePart2;
    }

    public BaseWeedItem(Properties pProperties, MobEffect effect, int iDuration, int iAmplifier, int iThc, int iCbd, boolean variableDuration) {
        super(pProperties);
        this.duration = iDuration;
        this.thcLevel = iThc;
        this.cbdLevel = iCbd;
        this.effect = effect;
        this.effectAmplifier = iAmplifier;
        this.variableDuration = variableDuration;
    }

    // Call this when creating a new ItemStack to sync fields to data components
    public void initializeStack(ItemStack stack) {
        stack.set(ModDataComponentTypes.ACTIVE_INGREDIENT.get(), BuiltInRegistries.MOB_EFFECT.getKey(this.effect).toString());
        stack.set(ModDataComponentTypes.EFFECT_DURATION.get(), this.duration);
        stack.set(ModDataComponentTypes.THC.get(), this.thcLevel);
        stack.set(ModDataComponentTypes.CBD.get(), this.cbdLevel);
    }

    /** Sets the name suffix appended after the strain name (e.g. " Weed", " Extract"). */
    public BaseWeedItem withNameSuffix(String suffix) {
        this.nameSuffix = suffix;
        return this;
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + nameSuffix);
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;

        tooltipComponents.add(getLevelsText(stack));

        List<MobEffectInstance> previews = buildEffectInstances(stack);
        if (!previews.isEmpty()) {
            MobEffectInstance first = previews.get(0);
            MobEffect baseEff = first.getEffect().value();
            int seconds = first.getDuration() / 20;
            tooltipComponents.add(
                    Component.translatable(baseEff.getDescriptionId())
                            .append(Component.literal(" (" + seconds + "s)").withStyle(ChatFormatting.GRAY))
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        if (previews.size() > 1) {
            MutableComponent joined = Component.empty();
            for (int i = 1; i < previews.size(); i++) {
                MobEffect extra = previews.get(i).getEffect().value();
                Component name = Component.translatable(extra.getDescriptionId()).withStyle(ChatFormatting.WHITE);
                if (i > 1) {
                    joined = joined.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                }
                joined = joined.append(name);
            }
            tooltipComponents.add(
                    Component.translatable("tooltip.smokeleafindustries.extra_effects", joined)
                            .withStyle(ChatFormatting.GRAY)
            );
        }
    }

    private Component getLevelsText(ItemStack stack) {
        int thc = getTHC(stack);
        int cbd = getCBD(stack);
        return Component.literal("Levels: ")
                .append(Component.literal(thc + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" THC").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" & ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(cbd + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" CBD").withStyle(ChatFormatting.DARK_GRAY));
    }

    // Data component-aware accessors (fallback to fields for legacy)
    // CBD-based unified duration override for all effects
    public int getDuration(ItemStack stack) {
        return computeUnifiedDurationTicks(stack);
    }

    public MobEffect getEffect(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && !d.effects().isEmpty()) {
            MobEffect strainEffect = BuiltInRegistries.MOB_EFFECT.get(d.effects().get(0));
            if (strainEffect != null) {
                return strainEffect;
            }
        }

        String effectId = stack.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        if (effectId == null) {
            effectId = BuiltInRegistries.MOB_EFFECT.getKey(this.effect).toString();
        }
        ResourceLocation effectRL = ResourceLocation.tryParse(effectId);
        if (effectRL != null) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectRL);
            if (effect != null) return effect;
        }
        return null;
    }

    public int getTHC(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY) {
            return d.thc();
        }
        Integer thc = stack.get(ModDataComponentTypes.THC.get());
        return thc != null ? thc : this.thcLevel;
    }

    public int getCBD(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY) {
            return d.cbd();
        }
        Integer cbd = stack.get(ModDataComponentTypes.CBD.get());
        return cbd != null ? cbd : this.cbdLevel;
    }

    // Unified duration derived from CBD, delegated to StrainEffectsUtil
    private int computeUnifiedDurationTicks(ItemStack stack) {
        return StrainEffectsUtil.computeDurationTicks(getCBD(stack), this.durationMultiplier);
    }

    public List<MobEffectInstance> buildEffectInstances(ItemStack stack) {
        return StrainEffectsUtil.buildEffectInstances(
                getTHC(stack), getCBD(stack), this.effectAmplifier,
                getEffect(stack), this.durationMultiplier, ADDITIONAL_EFFECT_POOL);
    }

    // Apply effects on consume/use
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            for (MobEffectInstance inst : buildEffectInstances(stack)) {
                if (inst != null && inst.getEffect() != null) {
                    entity.addEffect(inst);
                }
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }

    // Legacy field-based accessors for old code
    public int getDuration() { return this.duration; }
    public MobEffect getEffect() { return this.effect; }
    public int getEffectAmplifier() { return this.effectAmplifier; }
    public void setEffectAmplifier(int effectAmplifier) { this.effectAmplifier = effectAmplifier; }
    public boolean isVariableDuration() { return this.variableDuration; }
    public void setVariableDuration(boolean variableDuration) { this.variableDuration = variableDuration; }
    public String[] getWeedNameParts() { return weedNameParts; }
    public float getDurationMultiplier() { return durationMultiplier; }
    public void setDurationMultiplier(float durationMultiplier) { this.durationMultiplier = durationMultiplier; }
}
