package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.ExtractorRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtractorRecipeCategory implements IRecipeCategory<ExtractorRecipe> {
    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "extractor");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/extractor/extractor_gui.png");

    public static final RecipeType<ExtractorRecipe> EXTRACTOR_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ExtractorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ExtractorRecipeCategory(IGuiHelper helper) {
        // Ensure the region (5,5,168,75) fits inside the texture.
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 75);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.EXTRACTOR.get()));
    }

    @Override
    public RecipeType<ExtractorRecipe> getRecipeType() {
        return EXTRACTOR_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.extractor");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 75;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 75, 6)
                .addIngredients(recipe.getIngredients().getFirst());

        ItemStack result = recipe.getResultItem(null);
        List<ItemStack> outputs = (result.getItem() instanceof BaseWeedItem || result.getItem() instanceof BaseBudItem)
                ? JeiStrainHelper.coloredStacks(result.getItem())
                : List.of(result);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 54)
                .addItemStacks(outputs);
    }
}