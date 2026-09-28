package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record GeneratorRecipeInput(ItemStack stack) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }
}