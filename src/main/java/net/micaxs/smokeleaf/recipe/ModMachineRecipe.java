package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Defaults shared by every machine recipe type of this mod: none of them take part in the vanilla
 * recipe book (no unlock toasts, no placement), so they're all "special" recipes.
 */
public interface ModMachineRecipe<I extends RecipeInput> extends Recipe<I> {
    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default String group() {
        return "";
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return ModRecipes.MACHINE_BOOK_CATEGORY.get();
    }
}
