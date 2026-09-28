package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;

public record SequencerRecipeInput(ItemStack dna,
                                   ItemStack baseExtract) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> dna;
            case 1 -> baseExtract;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}