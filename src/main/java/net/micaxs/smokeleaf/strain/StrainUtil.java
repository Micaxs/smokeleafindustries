package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.WeedFluidStackUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class StrainUtil {
    private StrainUtil() {}

    public static final int DEFAULT_UNIDENTIFIED_COLOR = 0xFFAAAAAA;

    public static boolean hasStrain(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.has(ModDataComponentTypes.STRAIN_DATA.get());
    }

    public static StrainData getStrain(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return StrainData.EMPTY;
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        return d != null ? d : StrainData.EMPTY;
    }

    public static void setStrain(ItemStack stack, StrainData data) {
        if (stack == null || stack.isEmpty()) return;
        stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
    }

    public static boolean hasStrain(FluidStack stack) {
        return stack != null && !stack.isEmpty() && stack.has(ModDataComponentTypes.STRAIN_DATA.get());
    }

    public static StrainData getStrain(FluidStack stack) {
        if (stack == null || stack.isEmpty()) return StrainData.EMPTY;
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        return d != null ? d : StrainData.EMPTY;
    }

    public static void setStrain(FluidStack stack, StrainData data) {
        if (stack == null || stack.isEmpty()) return;
        stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
    }

    /**
     * Create a strain payload for the mixture using two input extract FluidStacks.
     *
     * MVP rules:
     * - effects: union (dedup, stable)
     * - amplifier: max
     * - duration: max
     * - color: weighted average by amount (uses fluid-type tint if per-stack strain color absent)
     */
    public static StrainData mixFromExtracts(FluidStack a, int aTint, FluidStack b, int bTint) {
        int amtA = Math.max(0, a == null ? 0 : a.getAmount());
        int amtB = Math.max(0, b == null ? 0 : b.getAmount());

        int cA = hasStrain(a) ? getStrain(a).colorArgb() : aTint;
        int cB = hasStrain(b) ? getStrain(b).colorArgb() : bTint;
        int leafA = hasStrain(a) ? getStrain(a).leafColor() : 0xFF4A7A2E;
        int leafB = hasStrain(b) ? getStrain(b).leafColor() : 0xFF4A7A2E;

        int mixedColor = mixColors(cA, amtA, cB, amtB);
        int mixedLeafColor = mixColors(leafA, amtA, leafB, amtB);

        // Effects union
        var effects = new LinkedHashSet<ResourceLocation>();
        var wa = (a != null) ? WeedFluidStackUtil.getWeedData(a) : null;
        var wb = (b != null) ? WeedFluidStackUtil.getWeedData(b) : null;
        if (wa != null) effects.addAll(wa.effects());
        if (wb != null) effects.addAll(wb.effects());
        List<ResourceLocation> effList = new ArrayList<>(effects);

        int amp = 0;
        int dur = 0;
        if (wa != null) { amp = Math.max(amp, wa.amplifier()); dur = Math.max(dur, wa.durationTicks()); }
        if (wb != null) { amp = Math.max(amp, wb.amplifier()); dur = Math.max(dur, wb.durationTicks()); }

        // N/P/K (and THC/CBD) derived from input extract stats.
        // Prefer STRAIN_DATA on the fluid stack (set by the liquifier for generic extracts).
        // Fall back to static ExtractFluidStats for named extract fluids.
        var sa = (a != null) ? ExtractFluidStats.get(a.getFluid()) : ExtractFluidStats.Stats.EMPTY;
        var sb = (b != null) ? ExtractFluidStats.get(b.getFluid()) : ExtractFluidStats.Stats.EMPTY;

        // If ExtractFluidStats has no data for this fluid, read from STRAIN_DATA component.
        if (sa == ExtractFluidStats.Stats.EMPTY && hasStrain(a)) {
            StrainData sda = getStrain(a);
            sa = new ExtractFluidStats.Stats(sda.nitrogen(), sda.phosphorus(), sda.potassium(), sda.thc(), sda.cbd());
        }
        if (sb == ExtractFluidStats.Stats.EMPTY && hasStrain(b)) {
            StrainData sdb = getStrain(b);
            sb = new ExtractFluidStats.Stats(sdb.nitrogen(), sdb.phosphorus(), sdb.potassium(), sdb.thc(), sdb.cbd());
        }
        int total = Math.max(1, amtA + amtB);

        int n = (sa.n() * amtA + sb.n() * amtB) / total;
        int p = (sa.p() * amtA + sb.p() * amtB) / total;
        int k = (sa.k() * amtA + sb.k() * amtB) / total;

        int thc = (sa.thc() * amtA + sb.thc() * amtB) / total;
        int cbd = (sa.cbd() * amtA + sb.cbd() * amtB) / total;

        return new StrainData(
                mixedColor,
                mixedLeafColor,
                thc,
                cbd,
                n,
                p,
                k,
                effList,
                amp,
                dur,
                false,
                "",
                StrainData.TypeColors.NONE
        );
    }

    /**
     * Ensures the mixture has stable stat rolls.
     *
     * If THC/CBD or N/P/K are unset (all zeros), we roll them once and return a new StrainData.
     * If any of those values are already present, we keep the data as-is.
     * When a strainId is provided, stat generation is deterministic (same ID → same stats).
     */
    public static StrainData finalizeMixtureStats(StrainData base, RandomSource random) {
        return finalizeMixtureStats(base, random, null);
    }

    public static StrainData finalizeMixtureStats(StrainData base, RandomSource random, @org.jetbrains.annotations.Nullable String strainId) {
        if (base == null || base == StrainData.EMPTY) return StrainData.EMPTY;

        boolean needsCannabinoids = base.thc() == 0 && base.cbd() == 0;
        boolean needsNpk = base.nitrogen() == 0 && base.phosphorus() == 0 && base.potassium() == 0;
        if (!needsCannabinoids && !needsNpk) return base;

        // If a strain ID is known, use a deterministic seed so the same strain always gets the same stats.
        final java.util.Random deterministicRng;
        if (strainId != null && !strainId.isBlank()) {
            long seed = strainId.chars().asLongStream().reduce(1L, (acc, c) -> acc * 31L + c);
            deterministicRng = new java.util.Random(seed);
        } else if (random != null) {
            deterministicRng = null;
        } else {
            return base;
        }

        java.util.function.IntSupplier rng31 = deterministicRng != null
                ? () -> deterministicRng.nextInt(31) : () -> random.nextInt(31);
        java.util.function.IntSupplier rng15 = deterministicRng != null
                ? () -> deterministicRng.nextInt(15) : () -> random.nextInt(15);

        int thc = needsCannabinoids ? Math.max(1, rng31.getAsInt()) : base.thc();
        int cbd = needsCannabinoids ? Math.max(1, rng31.getAsInt()) : base.cbd();
        int n = needsNpk ? Math.max(1, rng15.getAsInt()) : base.nitrogen();
        int p = needsNpk ? Math.max(1, rng15.getAsInt()) : base.phosphorus();
        int k = needsNpk ? Math.max(1, rng15.getAsInt()) : base.potassium();

        return new StrainData(
                base.colorArgb(),
                base.leafColor(),
                thc,
                cbd,
                n,
                p,
                k,
                base.effects(),
                base.amplifier(),
                base.durationTicks(),
                base.identified(),
                base.displayName(),
                base.typeColors()
        );
    }

    public static int mixColors(int argbA, int weightA, int argbB, int weightB) {
        int a = Math.max(0, weightA);
        int b = Math.max(0, weightB);
        int total = Math.max(1, a + b);

        int r = (getR(argbA) * a + getR(argbB) * b) / total;
        int g = (getG(argbA) * a + getG(argbB) * b) / total;
        int bl = (getB(argbA) * a + getB(argbB) * b) / total;
        int al = (getA(argbA) * a + getA(argbB) * b) / total;
        return (Mth.clamp(al, 0, 255) << 24) | (Mth.clamp(r, 0, 255) << 16) | (Mth.clamp(g, 0, 255) << 8) | Mth.clamp(bl, 0, 255);
    }

    public static int getA(int argb) { return (argb >>> 24) & 0xFF; }
    public static int getR(int argb) { return (argb >>> 16) & 0xFF; }
    public static int getG(int argb) { return (argb >>> 8) & 0xFF; }
    public static int getB(int argb) { return argb & 0xFF; }
}
