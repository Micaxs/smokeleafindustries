package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.crafting.RecipeMap;

/**
 * Client-side copy of the recipes the server synced to us (see {@code ModBusEvents#onDatapackSync}).
 * Recipes are no longer sent to clients by default, so anything client-side that needs them (JEI,
 * slot validity predictions, the guide book) reads them from here. Plain data holder, safe to load
 * on the server.
 */
public final class ClientRecipes {
    private static volatile RecipeMap map = RecipeMap.EMPTY;

    private ClientRecipes() {}

    public static RecipeMap map() {
        return map;
    }

    public static void set(RecipeMap recipes) {
        map = recipes;
    }
}
