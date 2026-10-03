// java
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
import net.micaxs.smokeleaf.item.custom.JointItem;
import net.micaxs.smokeleaf.recipe.JointRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class JointRecipeCategory implements IRecipeCategory<JointRecipe> {
    public static final RecipeType<JointRecipe> JOINT_RECIPE_TYPE =
            RecipeType.create(SmokeleafIndustries.MODID, "joint", JointRecipe.class);

    private static final ResourceLocation VANILLA_BG =
            ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final Component title;

    public JointRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(VANILLA_BG, 29, 16, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAPER));
        this.title = Component.translatable("jei.smokeleafindustries.category.joint");
    }

    @Override
    public RecipeType<JointRecipe> getRecipeType() { return JOINT_RECIPE_TYPE; }

    @Override
    public Component getTitle() { return title; }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JointRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addItemStack(new ItemStack(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 37).addItemStack(new ItemStack(Items.PAPER));

        var weeds = JeiStrainHelper.coloredStacks(ModItems.GENERIC_WEED.get(), focuses);
        var weedIngredient = Ingredient.of(weeds.stream());
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addIngredients(weedIngredient);
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 19).addIngredients(weedIngredient);

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 19).addItemStack(new ItemStack(recipe.getTobaccoItem()));

        List<ItemStack> outputs = JeiStrainHelper.coloredBlendOutputs(
                recipe.getResultItem(null).getItem(), 2, JointItem::storeWeeds, focuses);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19).addIngredients(Ingredient.of(outputs.stream()));
    }
}
