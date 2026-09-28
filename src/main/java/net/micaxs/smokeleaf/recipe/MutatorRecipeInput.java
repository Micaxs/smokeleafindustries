package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record MutatorRecipeInput(ItemStack seedInput, ItemStack extractInput) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> seedInput;
            case 1 -> extractInput;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
