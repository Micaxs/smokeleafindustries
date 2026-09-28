package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

/**
 * Single-slot input wrapper for Manual Grinder recipes.
 */
public class ManualGrinderInput implements RecipeInput {

    private final ItemStack stack;

    public ManualGrinderInput(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    public ItemStack stack() {
        return stack;
    }
}