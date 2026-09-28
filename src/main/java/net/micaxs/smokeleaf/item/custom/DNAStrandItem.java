package net.micaxs.smokeleaf.item.custom;

import org.jetbrains.annotations.Nullable;
import net.micaxs.smokeleaf.component.DNAContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.recipe.SequencerRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DNAStrandItem extends Item {


    public DNAStrandItem(Properties properties) {
        super(properties);
    }

    public static DNAContents getContents(ItemStack dnaStrand) {
        return ModDataComponentTypes.DNA_CONTENTS.getOrDefault(dnaStrand, DNAContents.EMPTY);
    }

    public static void setContents(ItemStack dnaStrand, DNAContents contents) {
        ModDataComponentTypes.DNA_CONTENTS.set(dnaStrand, contents);
    }

    public static boolean isFull(ItemStack dnaStrand) {
        return getContents(dnaStrand).isFull();
    }

    public static boolean addItem(ItemStack dnaStrand, ItemStack toInsert) {
        if (!(dnaStrand.getItem() instanceof DNAStrandItem) || toInsert.isEmpty()) return false;
        DNAContents contents = getContents(dnaStrand);

        for (int i = 0; i < 3; i++) {
            if (contents.get(i).isEmpty()) {
                setContents(dnaStrand, contents.with(i, toInsert.copyWithCount(1)));
                toInsert.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DNAContents contents = getContents(stack);
        int shown = 0;
        for (int i = 0; i < 3; i++) {
            ItemStack slot = contents.get(i);
            if (!slot.isEmpty()) {
                if (shown == 0) {
                    tooltipComponents.add(Component.literal("Contents:").withStyle(ChatFormatting.GOLD));
                }
                tooltipComponents.add(slot.getHoverName().copy().withStyle(ChatFormatting.AQUA));
                shown++;
            }
        }
        if (shown == 0) {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
        }

        Level level = context;
        if (level != null && contents.isFull()) {
            if (isValidCombination(level, contents)) {
                tooltipComponents.add(Component.literal("Valid Strain").withStyle(ChatFormatting.GREEN));
            } else {
                tooltipComponents.add(Component.literal("Invalid Strain").withStyle(ChatFormatting.RED));
            }
        }
    }


    private static boolean isValidCombination(Level level, DNAContents contents) {
        if (!contents.isFull()) return false;

        // Gather the three reagent stacks inside the DNA strand
        List<ItemStack> inside = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            inside.add(contents.get(i));
        }

        // Iterate all Sequencer recipes; we only compare against their requiredReagents[]
        List<SequencerRecipe> recipes =
                level.getRecipeManager().getAllRecipesFor(ModRecipes.SEQUENCER_TYPE.get());

        recipeLoop:
        for (SequencerRecipe holder : recipes) {
            SequencerRecipe recipe = holder;
            var required = recipe.requiredReagents(); // length == 3 enforced by serializer
            if (required.length != inside.size()) continue;

            boolean[] used = new boolean[inside.size()];

            // Unordered matching: each Ingredient must match a distinct reagent stack
            for (int r = 0; r < required.length; r++) {
                boolean matched = false;
                for (int i = 0; i < inside.size(); i++) {
                    if (!used[i] && required[r].test(inside.get(i))) {
                        used[i] = true;
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    continue recipeLoop;
                }
            }
            // All required matched
            return true;
        }
        return false;
    }

}
