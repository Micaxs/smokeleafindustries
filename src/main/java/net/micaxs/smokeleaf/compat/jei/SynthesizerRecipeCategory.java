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
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.DNAContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.recipe.SequencerRecipe;
import net.micaxs.smokeleaf.recipe.SynthesizerRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SynthesizerRecipeCategory implements IRecipeCategory<SynthesizerRecipeCategory.Display> {

    public static final ResourceLocation UID =
            new ResourceLocation(SmokeleafIndustries.MODID, "synthesizer");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(SmokeleafIndustries.MODID, "textures/gui/synthesizer/synthesizer_gui.png");
    public static final RecipeType<Display> SYNTHESIZER_RECIPE_TYPE =
            new RecipeType<>(UID, Display.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public SynthesizerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 75);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.SYNTHESIZER.get()));
        IDrawableStatic arrowStatic = helper.createDrawable(TEXTURE, 176, 0, 8, 26);
        this.arrow = helper.createAnimatedDrawable(arrowStatic, 120, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public RecipeType<Display> getRecipeType() {
        return SYNTHESIZER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.synthesizer");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(Display display, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        arrow.draw(guiGraphics, 125, 25);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Display display, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 75, 6).addItemStack(display.dnaInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 57, 30).addItemStack(display.reagent1());
        builder.addSlot(RecipeIngredientRole.INPUT, 75, 30).addItemStack(display.reagent2());
        builder.addSlot(RecipeIngredientRole.INPUT, 93, 30).addItemStack(display.reagent3());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 54).addItemStack(display.outputDNA());
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 75;
    }

    public static List<Display> buildValidStrainDisplays(SynthesizerRecipe base, List<SequencerRecipe> sequencerRecipes) {
        ItemStack emptyDNA = new ItemStack(ModItems.DNA_STRAND.get());
        Set<String> seenKeys = new HashSet<>();

        return sequencerRecipes.stream()
                .map(seq -> {
                    var ing = seq.requiredReagents();
                    if (ing.length != 3) return null;

                    ItemStack a = firstNonEmpty(ing[0]);
                    ItemStack b = firstNonEmpty(ing[1]);
                    ItemStack c = firstNonEmpty(ing[2]);
                    if (a.isEmpty() || b.isEmpty() || c.isEmpty()) return null;

                    String key = canonicalKey(a, b, c);
                    if (!seenKeys.add(key)) return null;

                    ItemStack output = new ItemStack(ModItems.DNA_STRAND.get());
                    ModDataComponentTypes.DNA_CONTENTS.set(output,
                            new DNAContents(a.copyWithCount(1), b.copyWithCount(1), c.copyWithCount(1)));

                    return new Display(base, emptyDNA.copy(), a.copy(), b.copy(), c.copy(), output);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static ItemStack firstNonEmpty(net.minecraft.world.item.crafting.Ingredient ingredient) {
        for (ItemStack s : ingredient.getItems()) {
            if (!s.isEmpty()) return s;
        }
        return ItemStack.EMPTY;
    }

    private static String canonicalKey(ItemStack a, ItemStack b, ItemStack c) {
        List<String> ids = new ArrayList<>(3);
        ids.add(BuiltInRegistries.ITEM.getKey(a.getItem()).toString());
        ids.add(BuiltInRegistries.ITEM.getKey(b.getItem()).toString());
        ids.add(BuiltInRegistries.ITEM.getKey(c.getItem()).toString());
        Collections.sort(ids);
        return String.join("|", ids);
    }

    public record Display(
            SynthesizerRecipe base,
            ItemStack dnaInput,
            ItemStack reagent1,
            ItemStack reagent2,
            ItemStack reagent3,
            ItemStack outputDNA
    ) { }
}