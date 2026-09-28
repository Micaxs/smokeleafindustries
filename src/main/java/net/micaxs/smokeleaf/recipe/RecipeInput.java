package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Read-only recipe input view (backport of the 1.21 {@code RecipeInput}). 1.20.1 recipes match
 * against a {@link Container}, so this supplies the container plumbing on top of
 * {@link #getItem(int)} / {@link #size()}.
 */
public interface RecipeInput extends Container {

    @Override
    ItemStack getItem(int index);

    int size();

    @Override
    default int getContainerSize() {
        return size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    default ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    default void setItem(int index, ItemStack stack) {
    }

    @Override
    default void setChanged() {
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    @Override
    default void clearContent() {
    }
}
