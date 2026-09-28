package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record DryingRecipeInput(ItemStack stack) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }
}