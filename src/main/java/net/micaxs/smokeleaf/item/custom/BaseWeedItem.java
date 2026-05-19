package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.utils.WeedEffectHelper;
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

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Weed");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY) {
            tooltipComponents.add(Component.literal("THC: " + d.thc() + "%  CBD: " + d.cbd() + "%"));
            tooltipComponents.add(Component.literal("NPK: " + d.nitrogen() + "/" + d.phosphorus() + "/" + d.potassium()));
        }

        List<MobEffectInstance> previews = buildEffectInstances(stack);
        if (previews.isEmpty()) {
            return;
        }

        MobEffectInstance first = previews.get(0);
        MobEffect eff = first.getEffect().value();
        int dur = first.getDuration();
        tooltipComponents.add(WeedEffectHelper.getEffectTooltip(eff, dur, true));

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(getLevelsText(stack));

        if (previews.size() > 1) {
            // use MutableComponent so .append(...) is available
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

        if (this.variableDuration) {
            tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.base_weed").withStyle(ChatFormatting.DARK_GRAY));
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

    // Unified duration derived from CBD, in ticks
    private int computeUnifiedDurationTicks(ItemStack stack) {
        int cbd = getCBD(stack);
        int seconds;
        if (cbd > 30) seconds = 60;
        else if (cbd > 15) seconds = 20;
        else seconds = 10; // cbd <= 15
        // Allow multiplier to tweak final duration if needed
        return (int) (seconds * 20 * this.durationMultiplier);
    }

    // Extra effects count derived from THC
    private int computeExtraEffectCount(int thc) {
        if (thc > 35) return 2;
        if (thc > 20 && thc < 35) return 1;
        return 0;
    }

    // Deterministic extra effects based on seed from item id, base effect id, THC, CBD
    private List<MobEffect> getDeterministicExtraEffects(ItemStack stack, int count) {
        if (count <= 0 || ADDITIONAL_EFFECT_POOL.isEmpty()) return List.of();

        MobEffect base = getEffect(stack);
        ResourceLocation baseId = base != null ? BuiltInRegistries.MOB_EFFECT.getKey(base) : null;

        int thc = getTHC(stack);
        int cbd = getCBD(stack);
        long seed = 1469598103934665603L; // FNV offset basis
        seed ^= (baseId != null ? baseId.toString().hashCode() : 0);
        seed = (seed * 1099511628211L) ^ thc;
        seed = (seed * 1099511628211L) ^ cbd;

        String activeStr = stack.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        if (activeStr != null) {
            seed = (seed * 1099511628211L) ^ activeStr.hashCode();
        }

        List<MobEffect> candidates = new ArrayList<>();
        for (ResourceLocation rl : ADDITIONAL_EFFECT_POOL) {
            if (rl == null) continue;
            if (baseId != null && rl.equals(baseId)) continue;
            MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(rl);
            if (eff != null) candidates.add(eff);
        }
        if (candidates.isEmpty()) return List.of();

        Collections.shuffle(candidates, new Random(seed));
        if (count >= candidates.size()) return List.copyOf(candidates);
        return List.copyOf(candidates.subList(0, count));
    }

    private static Holder<MobEffect> toHolder(MobEffect effect) {
        if (effect == null) return null;
        var registry = BuiltInRegistries.MOB_EFFECT;
        var keyOpt = registry.getResourceKey(effect);
        if (keyOpt.isPresent()) {
            return registry.getHolderOrThrow(keyOpt.get());
        }
        return Holder.direct(effect);
    }

    public List<MobEffectInstance> buildEffectInstances(ItemStack stack) {
        int durationTicks = computeUnifiedDurationTicks(stack);
        int amp = this.effectAmplifier;

        List<MobEffectInstance> out = new ArrayList<>();

        MobEffect base = getEffect(stack);
        Holder<MobEffect> baseHolder = toHolder(base);
        if (baseHolder != null) {
            out.add(new MobEffectInstance(baseHolder, durationTicks, amp, false, true, true));
        }

        int extraCount = computeExtraEffectCount(getTHC(stack));
        for (MobEffect eff : getDeterministicExtraEffects(stack, extraCount)) {
            Holder<MobEffect> effHolder = toHolder(eff);
            if (effHolder != null) {
                out.add(new MobEffectInstance(effHolder, durationTicks, amp, false, true, true));
            }
        }
        return out;
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
