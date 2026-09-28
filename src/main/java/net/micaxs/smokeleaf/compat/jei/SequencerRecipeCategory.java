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
import net.micaxs.smokeleaf.item.ModItems; // Adjust if your package differs
import net.micaxs.smokeleaf.recipe.SequencerRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SequencerRecipeCategory implements IRecipeCategory<SequencerRecipe> {

    public static final ResourceLocation UID =
            new ResourceLocation(SmokeleafIndustries.MODID, "sequencer");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(SmokeleafIndustries.MODID, "textures/gui/sequencer/sequencer_gui.png");

    public static final RecipeType<SequencerRecipe> SEQUENCER_RECIPE_TYPE =
            new RecipeType<>(UID, SequencerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public SequencerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 75);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.SEQUENCER.get()));
        IDrawableStatic arrowStatic = helper.createDrawable(TEXTURE, 0, 166, 37, 16);
        this.arrow = helper.createAnimatedDrawable(arrowStatic, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<SequencerRecipe> getRecipeType() {
        return SEQUENCER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.smokeleafindustries.sequencer");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(SequencerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        arrow.draw(guiGraphics, 57, 28);
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
    public void setRecipe(IRecipeLayoutBuilder builder, SequencerRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> filledDNA = buildFilledDNAStacks(recipe);
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 16).addItemStacks(filledDNA);

        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 21, 40)
                    .addIngredients(recipe.getIngredients().get(1));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 28)
                .addItemStack(recipe.getResultItem(null));
    }

    private static ItemStack newDNAStack() {
        return new ItemStack(ModItems.DNA_STRAND.get()); // Ensure this exists
    }

    private static List<ItemStack> buildFilledDNAStacks(SequencerRecipe recipe) {
        Ingredient[] reagents = recipe.requiredReagents();
        List<ItemStack> out = new ArrayList<>();
        if (reagents.length != 3) return out;

        ItemStack[] aSet = reagents[0].getItems();
        ItemStack[] bSet = reagents[1].getItems();
        ItemStack[] cSet = reagents[2].getItems();

        int limit = 128;
        int produced = 0;

        for (ItemStack a : aSet) {
            for (ItemStack b : bSet) {
                for (ItemStack c : cSet) {
                    ItemStack dna = newDNAStack();
                    DNAContents contents = new DNAContents(
                            a.copyWithCount(1),
                            b.copyWithCount(1),
                            c.copyWithCount(1)
                    );
                    ModDataComponentTypes.DNA_CONTENTS.set(dna, contents);
                    out.add(dna);
                    if (++produced >= limit) return out;
                }
            }
        }

        if (out.isEmpty()) {
            ItemStack dna = newDNAStack();
            ModDataComponentTypes.DNA_CONTENTS.set(dna, DNAContents.EMPTY);
            out.add(dna);
        }
        return out;
    }
}