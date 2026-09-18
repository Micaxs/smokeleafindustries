package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class GummyRecipeInput implements RecipeInput {
    private final ItemStack mold;
    private final ItemStack catalyst;

    public GummyRecipeInput(ItemStack mold, ItemStack catalyst) {
        this.mold = mold;
        this.catalyst = catalyst;
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> mold;
            case 1 -> catalyst;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
