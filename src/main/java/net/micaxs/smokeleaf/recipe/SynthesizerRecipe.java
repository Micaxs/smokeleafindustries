package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.micaxs.smokeleaf.component.DNAContents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SynthesizerRecipe(ResourceLocation id, Ingredient dnaIngredient,
                                ItemStack result) implements Recipe<SynthesizerRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
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
    public ItemStack assemble(SynthesizerRecipeInput input, RegistryAccess provider) {
        ItemStack dna = input.dna();
        if (!(dna.getItem() instanceof DNAStrandItem)) {
            return result.copy();
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

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SYNTHESIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SYNTHESIZER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(dnaIngredient);
        return list;
    }

    public static class Serializer implements CodecRecipeSerializer<SynthesizerRecipe> {

        @Override
        public MapCodec<SynthesizerRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(SynthesizerRecipe::dnaIngredient),
                            CodecCompat.ITEM_STACK.fieldOf("result").forGetter(SynthesizerRecipe::result)
                    ).apply(instance, (dna, result) -> new SynthesizerRecipe(id, dna, result))
            );
        }

        @Override
        public SynthesizerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient dna = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new SynthesizerRecipe(id, dna, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SynthesizerRecipe value) {
            value.dnaIngredient.toNetwork(buf);
            buf.writeItem(value.result);
        }
    }
}
