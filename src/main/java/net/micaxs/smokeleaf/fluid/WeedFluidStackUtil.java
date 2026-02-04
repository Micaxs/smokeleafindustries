package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * Helper methods for attaching and applying weed effects stored on FluidStacks.
 */
public final class WeedFluidStackUtil {
    private WeedFluidStackUtil() {}

    public static FluidStack withWeedData(FluidStack stack, List<ResourceLocation> effects, int amplifier, int durationTicks) {
        if (stack == null || stack.isEmpty()) return stack;
        stack.set(ModDataComponentTypes.WEED_FLUID_DATA.get(), new WeedFluidData(List.copyOf(effects), amplifier, durationTicks));
        return stack;
    }

    public static WeedFluidData getWeedData(FluidStack stack) {
        if (stack == null || stack.isEmpty()) return WeedFluidData.EMPTY;
        WeedFluidData data = stack.get(ModDataComponentTypes.WEED_FLUID_DATA.get());
        return data != null ? data : WeedFluidData.EMPTY;
    }

    public static void applyWeedEffects(FluidStack stack, LivingEntity entity) {
        WeedFluidData data = getWeedData(stack);
        if (data.effects().isEmpty()) return;

        for (ResourceLocation id : data.effects()) {
            if (id == null) continue;
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect == null) continue;
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
            entity.addEffect(new MobEffectInstance(holder, data.durationTicks(), data.amplifier()));
        }
    }
}
