package net.micaxs.smokeleaf.utils;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.minecraft.world.item.ItemStack;

public final class WeedDataUtil {
    private WeedDataUtil() {}

    public static void copyWeedComponents(ItemStack from, ItemStack to) {
        if (from == null || to == null) return;

        var active = ModDataComponentTypes.ACTIVE_INGREDIENT.get(from);
        var dur = ModDataComponentTypes.EFFECT_DURATION.get(from);
        var thc = ModDataComponentTypes.THC.get(from);
        var cbd = ModDataComponentTypes.CBD.get(from);
        var strain = ModDataComponentTypes.STRAIN_DATA.get(from);
        var strainId = ModDataComponentTypes.STRAIN_ID.get(from);

        if (active != null) ModDataComponentTypes.ACTIVE_INGREDIENT.set(to, active);
        if (dur != null) ModDataComponentTypes.EFFECT_DURATION.set(to, dur);
        if (thc != null) ModDataComponentTypes.THC.set(to, thc);
        if (cbd != null) ModDataComponentTypes.CBD.set(to, cbd);
        if (strain != null) ModDataComponentTypes.STRAIN_DATA.set(to, strain);
        if (strainId != null) ModDataComponentTypes.STRAIN_ID.set(to, strainId);
    }
}
