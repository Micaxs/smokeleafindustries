package net.micaxs.smokeleaf.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.ManualGrinderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ManualGrinderRecipeCategory implements IRecipeCategory<ManualGrinderRecipe> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "manual_grinder");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/jei_bg.png");

    public static final RecipeType<ManualGrinderRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, ManualGrinderRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated progress;

    public ManualGrinderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 110, 50);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.MANUAL_GRINDER.get()));
        IDrawableStatic arrow = guiHelper.createDrawable(TEXTURE, 110, 0, 24, 17);
        this.progress = guiHelper.createAnimatedDrawable(arrow, 40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<ManualGrinderRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.smokeleafindustries.manual_grinder");
    }


    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ManualGrinderRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 16)
                .addIngredients(recipe.getIngredients().getFirst());

        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.registryAccess() : null).copy();
        List<ItemStack> outputs = (result.getItem() instanceof BaseWeedItem || result.getItem() instanceof BaseBudItem)
                ? JeiStrainHelper.coloredStacks(result.getItem())
                : List.of(result);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 16)
                .addItemStacks(outputs);
    }

    @Override
    public void draw(ManualGrinderRecipe recipe, IRecipeSlotsView view,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        progress.draw(guiGraphics, 42, 16);
        Minecraft mc = Minecraft.getInstance();
        String ticks = recipe.grindTime() + " Ticks";
        guiGraphics.drawString(mc.font,
                Component.translatable("jei.smokeleafindustries.grind_time", ticks),
                22, 38, 0xB8B8B8, false);
    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 50;
    }
}