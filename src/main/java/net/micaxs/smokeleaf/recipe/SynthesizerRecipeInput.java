package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record SynthesizerRecipeInput(ItemStack dna,
                                     ItemStack reagent1,
                                     ItemStack reagent2,
                                     ItemStack reagent3) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> dna;
            case 1 -> reagent1;
            case 2 -> reagent2;
            case 3 -> reagent3;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }
}