package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record GrinderRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
