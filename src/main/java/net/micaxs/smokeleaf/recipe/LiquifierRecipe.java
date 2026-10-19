package net.micaxs.smokeleaf.recipe;

import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.level.material.Fluid;

public record LiquifierRecipe(Ingredient ingredient, FluidStackTemplate outputTemplate, boolean inheritInputEffects) implements ModMachineRecipe<LiquifierRecipeInput> {

    /** Fresh copy of the output (stored as a template: stacks can't be created while recipes load). */
    public FluidStack output() {
        return outputTemplate.create();
    }


    public LiquifierRecipe(Ingredient ingredient, FluidStackTemplate output) {
        this(ingredient, output, false);
    }

    public FluidStack outputCopy() {
        return output();
    }

    public boolean shouldInheritInputEffects() {
        return inheritInputEffects;
    }

    @Override
    public boolean matches(LiquifierRecipeInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(LiquifierRecipeInput input) {
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
    public RecipeSerializer<LiquifierRecipe> getSerializer() {
        return ModRecipes.LIQUIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<LiquifierRecipe> getType() {
        return ModRecipes.LIQUIFIER_TYPE.get();
    }

    public static class Serializer {

        // Nested codec for the "output" object
        public static final MapCodec<FluidStackTemplate> FLUID_STACK_OBJECT = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(t -> t.fluid().value()),
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStackTemplate::amount)
        ).apply(inst, (Fluid fluid, Integer amt) -> new FluidStackTemplate(fluid, amt)));

        public static final MapCodec<LiquifierRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(LiquifierRecipe::ingredient),
                FLUID_STACK_OBJECT.fieldOf("output").forGetter(LiquifierRecipe::outputTemplate),
                Codec.BOOL.optionalFieldOf("inherit_input_effects", false).forGetter(LiquifierRecipe::inheritInputEffects)
        ).apply(inst, LiquifierRecipe::new));

        // Stream codec for FluidStack (fluid id + varint amount)
        private static final StreamCodec<RegistryFriendlyByteBuf, FluidStackTemplate> FLUID_STACK_STREAM_CODEC = FluidStackTemplate.STREAM_CODEC;

        public static final StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, LiquifierRecipe::ingredient,
                        FLUID_STACK_STREAM_CODEC, LiquifierRecipe::outputTemplate,
                        ByteBufCodecs.BOOL, LiquifierRecipe::inheritInputEffects,
                        LiquifierRecipe::new
                );

        public MapCodec<LiquifierRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}