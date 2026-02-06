package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.WeedFluidStackUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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

        int mixedColor = mixColors(cA, amtA, cB, amtB);

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

        return new StrainData(
                mixedColor,
                0, 0,
                0, 0, 0,
                effList,
                amp,
                dur,
                false,
                ""
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
