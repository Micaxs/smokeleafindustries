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
import net.micaxs.smokeleaf.recipe.GummyRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class GummyMachineRecipeCategory implements IRecipeCategory<GummyRecipe> {
    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "gummy_machine");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/gummy_machine/gummy_machine_gui.png");
    public static final RecipeType<GummyRecipe> GUMMY_MACHINE_RECIPE_TYPE =
            new RecipeType<>(UID, GummyRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int FLUID_X = 3;
    private static final int FLUID_Y = 6;
    private static final int FLUID_W = 16;
    private static final int FLUID_H = 64;
    private static final int FLUID_CAPACITY = 8000;

    private static final int MOLD_X = 39;
    private static final int MOLD_Y = 12;

    private static final int CATALYST_X = 39;
    private static final int CATALYST_Y = 48;

    private static final int OUTPUT_X = 119;
    private static final int OUTPUT_Y = 30;

    public GummyMachineRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.GUMMY_MACHINE.get()));
    }

    @Override
    public RecipeType<GummyRecipe> getRecipeType() {
        return GUMMY_MACHINE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.gummy_machine");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GummyRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, MOLD_X, MOLD_Y)
                .addIngredients(recipe.mold());

        builder.addSlot(RecipeIngredientRole.INPUT, CATALYST_X, CATALYST_Y)
                .addIngredients(recipe.catalyst().asDisplayIngredient());

        FluidStack fluid = recipe.oil();
        if (!fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, FLUID_X, FLUID_Y)
                    .setFluidRenderer(FLUID_CAPACITY, false, FLUID_W, FLUID_H)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluid.copy());
        }

        ItemStack out = recipe.output();
        if (!out.isEmpty()) {
            if (JeiStrainHelper.isStrainItem(out.getItem())) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                        .addIngredients(net.minecraft.world.item.crafting.Ingredient.of(
                                JeiStrainHelper.coloredStacks(out.getItem(), focuses).stream()));
            } else {
                builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                        .addItemStack(out.copy());
            }
        }
    }

    @Override
    public void draw(GummyRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 80;
    }
}
