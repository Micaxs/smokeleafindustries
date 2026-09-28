package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record GeneratorRecipe(ResourceLocation id, Ingredient ingredient, int totalEnergy) implements Recipe<GeneratorRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
    }


    public static final int ENERGY_PER_TICK = 40;

    public int computedBurnTime() {
        return (int) Math.ceil(totalEnergy / (double) ENERGY_PER_TICK);
    }

    @Override
    public boolean matches(GeneratorRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(GeneratorRecipeInput input, RegistryAccess provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GENERATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GENERATOR_TYPE.get();
    }

    public static class Serializer implements CodecRecipeSerializer<GeneratorRecipe> {
        @Override
        public MapCodec<GeneratorRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(GeneratorRecipe::ingredient),
                    com.mojang.serialization.Codec.INT.fieldOf("total_energy").forGetter(GeneratorRecipe::totalEnergy)
            ).apply(inst, (ing, energy) -> new GeneratorRecipe(id, ing, energy)));
        }

        @Override
        public GeneratorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new GeneratorRecipe(id, Ingredient.fromNetwork(buf), buf.readVarInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GeneratorRecipe recipe) {
            recipe.ingredient().toNetwork(buf);
            buf.writeVarInt(recipe.totalEnergy());
        }
    }
}
