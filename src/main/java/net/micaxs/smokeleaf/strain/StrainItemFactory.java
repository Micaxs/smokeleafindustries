package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Convenience factory for creating generic strain-bearing item stacks.
 */
public final class StrainItemFactory {
    private StrainItemFactory() {
    }

    public static ItemStack bud(String strainId) {
        return create(ModItems.GENERIC_BUD.get(), strainId);
    }

    public static ItemStack weed(String strainId) {
        return create(ModItems.GENERIC_WEED.get(), strainId);
    }

    public static ItemStack seeds(String strainId) {
        return create(ModItems.GENERIC_SEEDS.get(), strainId);
    }

    public static ItemStack extract(String strainId) {
        return create(ModItems.GENERIC_EXTRACT.get(), strainId);
    }

    public static ItemStack create(Item item, String strainId) {
        ItemStack stack = new ItemStack(item);
        return applyPreset(stack, strainId);
    }

    public static ItemStack applyPreset(ItemStack stack, String strainId) {
        StrainData data = StrainRegistry.getRequired(strainId);
        StrainUtil.setStrain(stack, data);
        ModDataComponentTypes.STRAIN_ID.set(stack, strainId);
        ModDataComponentTypes.THC.set(stack, data.thc());
        ModDataComponentTypes.CBD.set(stack, data.cbd());
        ModDataComponentTypes.NITROGEN.set(stack, data.nitrogen());
        ModDataComponentTypes.PHOSPHORUS.set(stack, data.phosphorus());
        ModDataComponentTypes.POTASSIUM.set(stack, data.potassium());
        ModDataComponentTypes.EFFECT_DURATION.set(stack, data.durationTicks());
        if (!data.effects().isEmpty()) {
            ModDataComponentTypes.ACTIVE_INGREDIENT.set(stack, data.effects().get(0).toString());
        }
        return stack;
    }
}
