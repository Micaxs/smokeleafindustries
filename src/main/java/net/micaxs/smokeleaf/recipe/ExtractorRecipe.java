package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ExtractorRecipe(ResourceLocation id, Ingredient inputItem, ItemStack output) implements Recipe<ExtractorRecipeInput> {

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
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ExtractorRecipeInput extractorRecipeInput, Level level) {
        return inputItem.test(extractorRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ExtractorRecipeInput extractorRecipeInput, RegistryAccess provider) {
        ItemStack out = output.copy();
        ItemStack in = extractorRecipeInput.getItem(0);
        if (!in.isEmpty()) {
            StrainData sd = ModDataComponentTypes.STRAIN_DATA.get(in);
            if (sd != null) {
                ModDataComponentTypes.STRAIN_DATA.set(out, sd);
            } else {
                Integer thc = ModDataComponentTypes.THC.get(in);
                Integer cbd = ModDataComponentTypes.CBD.get(in);
                if (thc != null) ModDataComponentTypes.THC.set(out, thc);
                if (cbd != null) ModDataComponentTypes.CBD.set(out, cbd);
            }
        }
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.EXTRACTOR_TYPE.get();
    }


    public static class Serializer implements CodecRecipeSerializer<ExtractorRecipe> {
        @Override
        public MapCodec<ExtractorRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(ExtractorRecipe::inputItem),
                    CodecCompat.ITEM_STACK.fieldOf("result").forGetter(ExtractorRecipe::output)
            ).apply(inst, (ing, out) -> new ExtractorRecipe(id, ing, out)));
        }

        @Override
        public ExtractorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new ExtractorRecipe(id, Ingredient.fromNetwork(buf), buf.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ExtractorRecipe recipe) {
            recipe.inputItem().toNetwork(buf);
            buf.writeItem(recipe.output());
        }
    }
}
