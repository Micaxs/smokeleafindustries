package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.recipe.BluntRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BluntRecipeCategory implements IRecipeCategory<BluntRecipe> {

    public static final RecipeType<BluntRecipe> BLUNT_RECIPE_TYPE =
            RecipeType.create(SmokeleafIndustries.MODID, "blunt", BluntRecipe.class);

    private static final ResourceLocation VANILLA_BG =
            ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");

    private static List<ItemStack> weedStacks() {
        return JeiStrainHelper.coloredStacks(ModItems.GENERIC_WEED.get());
    }

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final Component title;

    public BluntRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(VANILLA_BG, 29, 16, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAPER));
        this.title = Component.translatable("jei.smokeleafindustries.category.blunt");
    }

    @Override
    public RecipeType<BluntRecipe> getRecipeType() {
        return BLUNT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BluntRecipe recipe, IFocusGroup focuses) {
        // Top row: paper
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(new ItemStack(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addItemStack(new ItemStack(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addItemStack(new ItemStack(Items.PAPER));

        // Middle row: weeds (tag)
        var weeds = weedStacks();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addItemStacks(weeds);
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 19).addItemStacks(weeds);
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 19).addItemStacks(weeds);

        // Bottom row: paper
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37).addItemStack(new ItemStack(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 37).addItemStack(new ItemStack(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 37).addItemStack(new ItemStack(Items.PAPER));

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19).addItemStack(recipe.getResultItem(null));
    }
}
