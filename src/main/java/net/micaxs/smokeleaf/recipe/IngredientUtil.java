package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Ingredients no longer expose their matching stacks directly; this rebuilds that list for display/scanning code. */
public final class IngredientUtil {
    private IngredientUtil() {}

    /** One stack (count 1) per item the ingredient accepts. */
    public static ItemStack[] stacks(Ingredient ingredient) {
        return ingredient.items().map(ItemStack::new).toArray(ItemStack[]::new);
    }
}
