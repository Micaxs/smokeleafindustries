package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.recipe.MutatorRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class MutatorRecipeCategory implements IRecipeCategory<MutatorRecipe> {
    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "mutator");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/mutator/mutator_gui.png");
    public static final RecipeType<MutatorRecipe> MUTATOR_RECIPE_TYPE =
            new RecipeType<>(UID, MutatorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int FLUID_X = 50;
    private static final int FLUID_Y = 10;
    private static final int FLUID_W = 16;
    private static final int FLUID_H = 61;
    private static final int FLUID_CAPACITY = 8000;

    private static final int SEED_X = 81;
    private static final int SEED_Y = 10;

    private static final int EXTRACT_X = 101;
    private static final int EXTRACT_Y = 10;

    private static final int OUTPUT_X = 81;
    private static final int OUTPUT_Y = 55;

    public MutatorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.MUTATOR.get()));
    }

    @Override
    public RecipeType<MutatorRecipe> getRecipeType() {
        return MUTATOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.mutator");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MutatorRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();
        if (ingredients.size() > 0) {
            var seedIngredient = ingredients.get(0);
            if (JeiStrainHelper.isStrainIngredient(seedIngredient)) {
                builder.addSlot(RecipeIngredientRole.INPUT, SEED_X, SEED_Y)
                        .addIngredients(JeiStrainHelper.coloredIngredient(seedIngredient, focuses));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, SEED_X, SEED_Y)
                        .addIngredients(seedIngredient);
            }
        }
        if (ingredients.size() > 1) {
            var extractIngredient = ingredients.get(1);
            if (JeiStrainHelper.isStrainIngredient(extractIngredient)) {
                builder.addSlot(RecipeIngredientRole.INPUT, EXTRACT_X, EXTRACT_Y)
                        .addIngredients(JeiStrainHelper.coloredIngredient(extractIngredient, focuses));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, EXTRACT_X, EXTRACT_Y)
                        .addIngredients(extractIngredient);
            }
        }

        FluidStack fluid = recipe.getFluid();
        if (!fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, FLUID_X, FLUID_Y)
                    .setFluidRenderer(FLUID_CAPACITY, false, FLUID_W, FLUID_H)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluid.copy());
        }

        ItemStack out = recipe.output();
        if (!out.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                    .addItemStack(out.copy());
        }
    }

    @Override
    public void draw(MutatorRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 90;
    }
}