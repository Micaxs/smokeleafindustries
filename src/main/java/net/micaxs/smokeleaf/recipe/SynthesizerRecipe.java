package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.component.DNAContents;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SynthesizerRecipe(Ingredient dnaIngredient,
                                ItemStackTemplate resultTemplate) implements ModMachineRecipe<SynthesizerRecipeInput> {

    /** Fresh copy of the result (stored as a template: stacks can't be created while recipes load). */
    public ItemStack result() {
        return resultTemplate.create();
    }


    @Override
    public boolean matches(SynthesizerRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        if (!dnaIngredient.test(input.dna())) return false;
        // Require all 3 reagent slots filled (change to allow partial if desired)
        return !input.reagent1().isEmpty()
                && !input.reagent2().isEmpty()
                && !input.reagent3().isEmpty();
    }

    @Override
    public ItemStack assemble(SynthesizerRecipeInput input) {
        ItemStack dna = input.dna();
        if (!(dna.getItem() instanceof DNAStrandItem)) {
            return result().copy();
        }
        ItemStack filled = dna.copyWithCount(1);

        DNAContents contents = DNAContents.EMPTY;
        ItemStack[] reagents = { input.reagent1(), input.reagent2(), input.reagent3() };
        for (int i = 0; i < 3; i++) {
            if (!reagents[i].isEmpty()) {
                contents = contents.with(i, reagents[i].copyWithCount(1));
            }
        }
        DNAStrandItem.setContents(filled, contents);
        return filled;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result().copy();
    }

    @Override
    public RecipeSerializer<SynthesizerRecipe> getSerializer() {
        return ModRecipes.SYNTHESIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<SynthesizerRecipe> getType() {
        return ModRecipes.SYNTHESIZER_TYPE.get();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(dnaIngredient);
        return list;
    }

    public static class Serializer {

        public static final MapCodec<SynthesizerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(SynthesizerRecipe::dnaIngredient),
                        ItemStackTemplate.CODEC.fieldOf("result").forGetter(SynthesizerRecipe::resultTemplate)
                ).apply(instance, SynthesizerRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SynthesizerRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public SynthesizerRecipe decode(RegistryFriendlyByteBuf buf) {
                        Ingredient dna = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
                        return new SynthesizerRecipe(dna, result);
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, SynthesizerRecipe value) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, value.dnaIngredient);
                        ItemStackTemplate.STREAM_CODEC.encode(buf, value.resultTemplate);
                    }
                };

        public MapCodec<SynthesizerRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, SynthesizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}