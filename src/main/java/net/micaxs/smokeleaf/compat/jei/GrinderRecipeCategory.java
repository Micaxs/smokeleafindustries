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
import net.micaxs.smokeleaf.recipe.GrinderRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrinderRecipeCategory implements IRecipeCategory<GrinderRecipe> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "grinder");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/grinder/grinder_gui.png");

    public static final RecipeType<GrinderRecipe> GRINDER_RECIPE_TYPE =
            new RecipeType<>(UID, GrinderRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public GrinderRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 75);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.GRINDER.get()));
    }

    @Override
    public RecipeType<GrinderRecipe> getRecipeType() {
        return GRINDER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.grinder");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 75, 6)
                .addIngredients(recipe.getIngredients().get(0));

        ItemStack result = recipe.getResultItem(null);
        List<ItemStack> outputs = (result.getItem() instanceof BaseWeedItem || result.getItem() instanceof BaseBudItem)
                ? JeiStrainHelper.coloredStacks(result.getItem())
                : List.of(result);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 54)
                .addItemStacks(outputs);
    }

    @Override
    public void draw(GrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public int getWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 75;
    }
}