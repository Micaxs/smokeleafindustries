package net.micaxs.smokeleaf.utils;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.minecraft.world.item.ItemStack;

public final class WeedDataUtil {
    private WeedDataUtil() {}

    public static void copyWeedComponents(ItemStack from, ItemStack to) {
        if (from == null || to == null) return;

        var active = from.get(ModDataComponentTypes.ACTIVE_INGREDIENT.get());
        var dur = from.get(ModDataComponentTypes.EFFECT_DURATION.get());
        var thc = from.get(ModDataComponentTypes.THC.get());
        var cbd = from.get(ModDataComponentTypes.CBD.get());
        var strain = from.get(ModDataComponentTypes.STRAIN_DATA.get());
        var strainId = from.get(ModDataComponentTypes.STRAIN_ID.get());

        if (active != null) to.set(ModDataComponentTypes.ACTIVE_INGREDIENT.get(), active);
        if (dur != null) to.set(ModDataComponentTypes.EFFECT_DURATION.get(), dur);
        if (thc != null) to.set(ModDataComponentTypes.THC.get(), thc);
        if (cbd != null) to.set(ModDataComponentTypes.CBD.get(), cbd);
        if (strain != null) to.set(ModDataComponentTypes.STRAIN_DATA.get(), strain);
        if (strainId != null) to.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
    }
}
