package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record GeneratorRecipe(Ingredient ingredient, int totalEnergy) implements ModMachineRecipe<GeneratorRecipeInput> {

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
    public ItemStack assemble(GeneratorRecipeInput input) {
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<GeneratorRecipe> getSerializer() {
        return ModRecipes.GENERATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<GeneratorRecipe> getType() {
        return ModRecipes.GENERATOR_TYPE.get();
    }

    public static class Serializer {
        public static final MapCodec<GeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(GeneratorRecipe::ingredient),
                com.mojang.serialization.Codec.INT.fieldOf("total_energy").forGetter(GeneratorRecipe::totalEnergy)
        ).apply(inst, GeneratorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, GeneratorRecipe::ingredient,
                        ByteBufCodecs.VAR_INT, GeneratorRecipe::totalEnergy,
                        GeneratorRecipe::new
                );

        public MapCodec<GeneratorRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}