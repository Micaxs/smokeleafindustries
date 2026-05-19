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
        stack.set(ModDataComponentTypes.THC.get(), data.thc());
        stack.set(ModDataComponentTypes.CBD.get(), data.cbd());
        stack.set(ModDataComponentTypes.NITROGEN.get(), data.nitrogen());
        stack.set(ModDataComponentTypes.PHOSPHORUS.get(), data.phosphorus());
        stack.set(ModDataComponentTypes.POTASSIUM.get(), data.potassium());
        stack.set(ModDataComponentTypes.EFFECT_DURATION.get(), data.durationTicks());
        if (!data.effects().isEmpty()) {
            stack.set(ModDataComponentTypes.ACTIVE_INGREDIENT.get(), data.effects().get(0).toString());
        }
        return stack;
    }
}
