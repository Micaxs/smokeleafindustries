package net.micaxs.smokeleaf.recipe;

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

public record LiquifierRecipe(Ingredient ingredient, FluidStack output, boolean inheritInputEffects) implements Recipe<LiquifierRecipeInput> {

    public LiquifierRecipe(Ingredient ingredient, FluidStack output) {
        this(ingredient, output, false);
    }

    public FluidStack outputCopy() {
        return output.copy();
    }

    public boolean shouldInheritInputEffects() {
        return inheritInputEffects;
    }

    @Override
    public boolean matches(LiquifierRecipeInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(LiquifierRecipeInput input, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
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
        return ModRecipes.LIQUIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LIQUIFIER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<LiquifierRecipe> {

        // Nested codec for the "output" object
        private static final MapCodec<FluidStack> FLUID_STACK_OBJECT = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
                Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount)
        ).apply(inst, (Fluid fluid, Integer amt) -> new FluidStack(fluid, amt)));

        public static final MapCodec<LiquifierRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(LiquifierRecipe::ingredient),
                FLUID_STACK_OBJECT.fieldOf("output").forGetter(LiquifierRecipe::output),
                Codec.BOOL.optionalFieldOf("inherit_input_effects", false).forGetter(LiquifierRecipe::inheritInputEffects)
        ).apply(inst, LiquifierRecipe::new));

        // Stream codec for FluidStack (fluid id + varint amount)
        private static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> FLUID_STACK_STREAM_CODEC =
                StreamCodec.of(
                        (buf, stack) -> {
                            ByteBufCodecs.idMapper(BuiltInRegistries.FLUID).encode(buf, stack.getFluid());
                            buf.writeVarInt(stack.getAmount());
                        },
                        buf -> {
                            Fluid f = ByteBufCodecs.idMapper(BuiltInRegistries.FLUID).decode(buf);
                            int amt = buf.readVarInt();
                            return new FluidStack(f, amt);
                        }
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, LiquifierRecipe::ingredient,
                        FLUID_STACK_STREAM_CODEC, LiquifierRecipe::output,
                        ByteBufCodecs.BOOL, LiquifierRecipe::inheritInputEffects,
                        LiquifierRecipe::new
                );

        @Override
        public MapCodec<LiquifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}