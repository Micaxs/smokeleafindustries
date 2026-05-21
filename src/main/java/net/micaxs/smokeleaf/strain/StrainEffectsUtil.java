package net.micaxs.smokeleaf.strain;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Global utility for building MobEffectInstance lists from StrainData.
 *
 * Rules:
 * - CBD linearly scales duration: 10 s (cbd=0) → 60 s (cbd=100).
 * - THC controls extra effect count (on top of the base effect):
 *     0-20%  → 0 extras
 *    21-40%  → 1 extra
 *    41-70%  → 2 extras
 *    71%+    → 3 extras (capped by pool size)
 * - Extra effects are chosen deterministically from the pool so the same
 *   strain always gets the same extras regardless of when it is consumed.
 *
 * Used by BaseWeedItem, BaseBudItem (future), and fluid/extract pipelines.
 */
public final class StrainEffectsUtil {

    private StrainEffectsUtil() {}

    // -----------------------------------------------------------------------
    // Duration
    // -----------------------------------------------------------------------

    /**
     * Convert a CBD percentage to a duration in ticks.
     * Scales linearly: cbd=0 → 200 ticks (10 s), cbd=100 → 1200 ticks (60 s).
     * An optional multiplier lets callers tweak the base (e.g. for weed vs extract).
     */
    public static int computeDurationTicks(int cbd, float multiplier) {
        double seconds = 10.0 + Mth.clamp(cbd, 0, 100) * 0.5;
        return (int) (seconds * 20.0 * multiplier);
    }

    // -----------------------------------------------------------------------
    // Extra effects
    // -----------------------------------------------------------------------

    /** Number of extra (side) effects derived from THC. */
    public static int computeExtraEffectCount(int thc) {
        if (thc > 70) return 3;
        if (thc > 40) return 2;
        if (thc > 20) return 1;
        return 0;
    }

    /**
     * Deterministically pick {@code count} extra effects from {@code pool},
     * excluding the base effect so it is never doubled.
     * The seed is derived from the base-effect id, THC, and CBD so the same
     * strain always yields the same side-effect set.
     */
    public static List<MobEffect> getDeterministicExtras(
            MobEffect baseEffect, int thc, int cbd, int count, List<ResourceLocation> pool) {

        if (count <= 0 || pool.isEmpty()) return List.of();

        ResourceLocation baseId = baseEffect != null ? BuiltInRegistries.MOB_EFFECT.getKey(baseEffect) : null;

        long seed = 1469598103934665603L;
        seed ^= (baseId != null ? baseId.toString().hashCode() : 0);
        seed = (seed * 1099511628211L) ^ thc;
        seed = (seed * 1099511628211L) ^ cbd;

        List<MobEffect> candidates = new ArrayList<>();
        for (ResourceLocation rl : pool) {
            if (rl == null) continue;
            if (baseId != null && rl.equals(baseId)) continue;
            MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(rl);
            if (eff != null) candidates.add(eff);
        }
        if (candidates.isEmpty()) return List.of();

        Collections.shuffle(candidates, new Random(seed));
        int take = Math.min(count, candidates.size());
        return List.copyOf(candidates.subList(0, take));
    }

    // -----------------------------------------------------------------------
    // Instance builder
    // -----------------------------------------------------------------------

    /**
     * Build the full list of MobEffectInstances for a given strain.
     *
     * @param thc             THC percentage (after nutrient scaling)
     * @param cbd             CBD percentage (after nutrient scaling)
     * @param amplifier       base amplifier (0 = level 1)
     * @param baseEffect      the primary MobEffect for this item/strain
     * @param durationMult    duration multiplier (1f for weed, higher for extracts)
     * @param additionalPool  pool of candidate extra-effect ResourceLocations
     */
    public static List<MobEffectInstance> buildEffectInstances(
            int thc, int cbd, int amplifier,
            MobEffect baseEffect,
            float durationMult,
            List<ResourceLocation> additionalPool) {

        int durationTicks = computeDurationTicks(cbd, durationMult);
        List<MobEffectInstance> out = new ArrayList<>();

        if (baseEffect != null) {
            Holder<MobEffect> holder = toHolder(baseEffect);
            if (holder != null) {
                out.add(new MobEffectInstance(holder, durationTicks, amplifier, false, true, true));
            }
        }

        int extraCount = computeExtraEffectCount(thc);
        for (MobEffect extra : getDeterministicExtras(baseEffect, thc, cbd, extraCount, additionalPool)) {
            Holder<MobEffect> holder = toHolder(extra);
            if (holder != null) {
                out.add(new MobEffectInstance(holder, durationTicks, amplifier, false, true, true));
            }
        }

        return out;
    }

    /**
     * Build MobEffectInstances directly from a list of effect ResourceLocations stored in StrainData.
     * Used when the strain has explicit effects from mixing (ignores THC-based pool selection).
     * All effects share the same CBD-derived duration and amplifier.
     */
    public static List<MobEffectInstance> buildEffectInstancesFromList(
            int cbd, int amplifier,
            List<ResourceLocation> effectIds,
            float durationMult) {

        int durationTicks = computeDurationTicks(cbd, durationMult);
        List<MobEffectInstance> out = new ArrayList<>();
        for (ResourceLocation rl : effectIds) {
            if (rl == null) continue;
            MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(rl);
            if (eff == null) continue;
            Holder<MobEffect> holder = toHolder(eff);
            if (holder != null) {
                out.add(new MobEffectInstance(holder, durationTicks, amplifier, false, true, true));
            }
        }
        return out;
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private static Holder<MobEffect> toHolder(MobEffect effect) {
        if (effect == null) return null;
        var keyOpt = BuiltInRegistries.MOB_EFFECT.getResourceKey(effect);
        return keyOpt.map(k -> (Holder<MobEffect>) BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(k))
                     .orElseGet(() -> Holder.direct(effect));
    }
}
