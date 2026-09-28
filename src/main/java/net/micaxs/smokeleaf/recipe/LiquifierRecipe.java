package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.world.level.material.Fluid;

public record LiquifierRecipe(ResourceLocation id, Ingredient ingredient, FluidStack output, boolean inheritInputEffects) implements Recipe<LiquifierRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
    }


    public LiquifierRecipe(ResourceLocation id, Ingredient ingredient, FluidStack output) {
        this(id, ingredient, output, false);
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
    public ItemStack assemble(LiquifierRecipeInput input, RegistryAccess provider) {
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
        return ModRecipes.LIQUIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LIQUIFIER_TYPE.get();
    }

    public static class Serializer implements CodecRecipeSerializer<LiquifierRecipe> {

        @Override
        public MapCodec<LiquifierRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(LiquifierRecipe::ingredient),
                    CodecCompat.FLUID_STACK_MAP.fieldOf("output").forGetter(LiquifierRecipe::output),
                    Codec.BOOL.optionalFieldOf("inherit_input_effects", false).forGetter(LiquifierRecipe::inheritInputEffects)
            ).apply(inst, (ing, out, inherit) -> new LiquifierRecipe(id, ing, out, inherit)));
        }

        @Override
        public LiquifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ing = Ingredient.fromNetwork(buf);
            FluidStack out = FluidStack.readFromPacket(buf);
            boolean inherit = buf.readBoolean();
            return new LiquifierRecipe(id, ing, out, inherit);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, LiquifierRecipe recipe) {
            recipe.ingredient().toNetwork(buf);
            recipe.output().writeToPacket(buf);
            buf.writeBoolean(recipe.inheritInputEffects());
        }
    }
}
