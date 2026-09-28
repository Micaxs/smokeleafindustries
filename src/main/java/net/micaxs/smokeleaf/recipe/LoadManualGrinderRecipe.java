package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.micaxs.smokeleaf.component.ManualGrinderContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.ManualGrinderItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LoadManualGrinderRecipe extends CustomRecipe {

    public static final int MAX_STORED = 3;

    public LoadManualGrinderRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        ItemStack grinder = ItemStack.EMPTY;
        ItemStack ingredient = ItemStack.EMPTY;
        int slotCount = 0;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ManualGrinderItem) {
                if (!grinder.isEmpty()) return false;
                if (ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.has(stack)) return false;
                grinder = stack;
            } else {
                // Any stack size is fine here — CommonEvents#onManualGrinderCraft tops up
                // the vanilla grid's "-1 per slot" removal to match how much actually gets
                // stored. But the number of *slots* is capped at MAX_STORED, since that's
                // how many of them vanilla will each dock by 1 when the result is taken.
                if (ingredient.isEmpty()) {
                    if (!hasManualGrinderRecipe(level, stack)) return false;
                    ingredient = stack;
                } else if (!ItemStack.isSameItemSameTags(ingredient, stack)) {
                    return false;
                }
                slotCount++;
            }
        }
        return !grinder.isEmpty() && !ingredient.isEmpty() && slotCount <= MAX_STORED;
    }

    private boolean hasManualGrinderRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) return false;
        ManualGrinderInput in = new ManualGrinderInput(stack.copyWithCount(1));
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.MANUAL_GRINDER_TYPE.get(), in, level)
                .isPresent();
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess provider) {
        ItemStack grinder = ItemStack.EMPTY;
        ItemStack ingredient = ItemStack.EMPTY;
        int totalCount = 0;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ManualGrinderItem) {
                grinder = stack;
            } else {
                if (ingredient.isEmpty()) ingredient = stack;
                totalCount += stack.getCount();
            }
        }
        if (grinder.isEmpty() || ingredient.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = grinder.copy();
        result.setCount(1);

        // Store the full input stack (all data components, incl. THC/CBD)
        ItemStack stored = ingredient.copyWithCount(Math.min(totalCount, MAX_STORED));
        ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.set(result,
                ManualGrinderContents.fromStack(stored));

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends SimpleCraftingRecipeSerializer<LoadManualGrinderRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
            super(LoadManualGrinderRecipe::new);
        }
    }
}
