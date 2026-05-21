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
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.BaseBudItem;
import net.micaxs.smokeleaf.recipe.DryingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DryingRecipeCategory implements IRecipeCategory<DryingRecipe> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "drying");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/jei_bg.png");

    public static final RecipeType<DryingRecipe> DRYING_RECIPE_TYPE =
            new RecipeType<>(UID, DryingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DryingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 110, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.DRYING_RACK.get()));
    }

    @Override
    public RecipeType<DryingRecipe> getRecipeType() {
        return DRYING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.drying_rack");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 16)
                .addIngredients(recipe.getIngredients().getFirst());

        ItemStack result = recipe.result();
        if (!result.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 16)
                    .addItemStack(result.copy());
            return;
        }

        ItemStack focusedInput = focuses.getFocuses(VanillaTypes.ITEM_STACK)
                .filter(f -> f.getRole() == RecipeIngredientRole.INPUT)
                .map(f -> {
                    try {
                        var m = f.getClass().getMethod("getIngredient");
                        Object v = m.invoke(f);
                        if (v instanceof ItemStack s) return s;
                    } catch (Exception ignored) {}
                    try {
                        var tv = f.getClass().getMethod("getTypedValue").invoke(f);
                        if (tv != null) {
                            var m2 = tv.getClass().getMethod("getIngredient");
                            Object v2 = m2.invoke(tv);
                            if (v2 instanceof ItemStack s2) return s2;
                        }
                    } catch (Exception ignored) {}
                    return ItemStack.EMPTY;
                })
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);

        if (!focusedInput.isEmpty() && focusedInput.getItem() instanceof BaseBudItem) {
            ItemStack dried = focusedInput.copy();
            dried.set(ModDataComponentTypes.DRY.get(), Boolean.TRUE);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 16)
                    .addItemStack(dried);
        } else {
            List<ItemStack> driedVariants = JeiStrainHelper.coloredDriedBudStacks(ModItems.GENERIC_BUD.get());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 16)
                    .addItemStacks(driedVariants);
        }
    }

    @Override
    public void draw(DryingRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        int ticks = recipe.time();
        String timeStr = ticks + " ticks";

        Minecraft mc = Minecraft.getInstance();
        guiGraphics.drawString(mc.font,
                Component.translatable("jei.smokeleafindustries.drying.time", timeStr),
                30, 20, 0xb8b8b8, false);
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