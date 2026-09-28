package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import java.util.stream.Stream;

/**
 * Mold + catalyst (sugar) + oil -> gummy. The mold and catalyst ingredients are checked in
 * {@link #matches}; the oil fluid type/amount is validated in the block entity's tick loop,
 * matching every other fluid-consuming machine recipe in this mod (Liquifier, Mutator).
 */
public record GummyRecipe(ResourceLocation id, Ingredient mold, IngredientWithCount catalyst, FluidStack oil, ItemStack output) implements Recipe<GummyRecipeInput> {

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
    public boolean matches(GummyRecipeInput input, Level level) {
        return mold.test(input.getItem(0))
                && catalyst.ingredient().test(input.getItem(1))
                && input.getItem(1).getCount() >= catalyst.count();
    }

    @Override
    public ItemStack assemble(GummyRecipeInput input, RegistryAccess provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(mold);
        list.add(catalyst.ingredient());
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GUMMY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GUMMY_TYPE.get();
    }

    public static class Serializer implements CodecRecipeSerializer<GummyRecipe> {

        @Override
        public MapCodec<GummyRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("mold").forGetter(GummyRecipe::mold),
                    IngredientWithCount.CODEC.fieldOf("catalyst").forGetter(GummyRecipe::catalyst),
                    CodecCompat.FLUID_STACK_MAP.fieldOf("oil").forGetter(GummyRecipe::oil),
                    CodecCompat.ITEM_STACK.fieldOf("output").forGetter(GummyRecipe::output)
            ).apply(inst, (mold, catalyst, oil, output) -> new GummyRecipe(id, mold, catalyst, oil, output)));
        }

        @Override
        public GummyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient mold = Ingredient.fromNetwork(buf);
            IngredientWithCount catalyst = IngredientWithCount.fromNetwork(buf);
            FluidStack oil = FluidStack.readFromPacket(buf);
            ItemStack output = buf.readItem();
            return new GummyRecipe(id, mold, catalyst, oil, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GummyRecipe recipe) {
            recipe.mold().toNetwork(buf);
            recipe.catalyst().toNetwork(buf);
            recipe.oil().writeToPacket(buf);
            buf.writeItem(recipe.output());
        }
    }
}
