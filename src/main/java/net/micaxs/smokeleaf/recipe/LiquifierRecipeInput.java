package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public class LiquifierRecipeInput implements RecipeInput {
    private final ItemStack stack;

    public LiquifierRecipeInput(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}