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
import net.micaxs.smokeleaf.recipe.GeneratorRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GeneratorRecipeCategory implements IRecipeCategory<GeneratorRecipe> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "generator");
    // Adjust texture path / size to an existing texture or create one
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/jei_bg.png");

    public static final RecipeType<GeneratorRecipe> GENERATOR_RECIPE_TYPE =
            new RecipeType<>(UID, GeneratorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public GeneratorRecipeCategory(IGuiHelper helper) {
        // Use a sub-region of your texture (fallback size 120x46; adjust as needed)
        this.background = helper.createDrawable(TEXTURE, 5, 5, 120, 46);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.GENERATOR.get()));
    }

    @Override
    public RecipeType<GeneratorRecipe> getRecipeType() {
        return GENERATOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.generator");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 46;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorRecipe recipe, IFocusGroup focuses) {
        var inputIngredient = recipe.getIngredients().getFirst();
        if (JeiStrainHelper.isStrainIngredient(inputIngredient)) {
            builder.addSlot(RecipeIngredientRole.INPUT, 14, 14)
                    .addIngredients(JeiStrainHelper.coloredIngredient(inputIngredient, focuses));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 14, 14)
                    .addIngredients(inputIngredient);
        }
    }

    @Override
    public void draw(GeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        int total = recipe.totalEnergy();
        int ticks = recipe.computedBurnTime();
        String energyLine = total + " FE";
        String ticksLine = ticks + " ticks";

        guiGraphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                energyLine, 40, 12, 0xFFD37F, false);
        guiGraphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                ticksLine, 40, 24, 0xFFFFFF, false);
    }
}