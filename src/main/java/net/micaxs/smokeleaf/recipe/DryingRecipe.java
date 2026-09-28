package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Drying recipe:
 * - ingredient: input item (bud, leaf, etc.)
 * - result: produced item (may be empty if only drying in-place, e.g. bud dry flag)
 * - time: ticks needed (default 200)
 * - dryBud: if true, block entity will set bud dry flag instead of replacing stack
 */
public record DryingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int time, boolean dryBud)
        implements Recipe<DryingRecipeInput> {

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
    public boolean matches(DryingRecipeInput input, Level level) {
        //if (level.isClientSide()) return false;
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(DryingRecipeInput input, RegistryAccess provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DRYING_TYPE.get();
    }

    public static class Serializer implements CodecRecipeSerializer<DryingRecipe> {

        // JSON codec (result optional)
        @Override
        public MapCodec<DryingRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(DryingRecipe::ingredient),
                    CodecCompat.ITEM_STACK.optionalFieldOf("result")
                            .forGetter(r -> r.result().isEmpty() ? Optional.empty() : Optional.of(r.result())),
                    Codec.INT.optionalFieldOf("time", 200).forGetter(DryingRecipe::time),
                    Codec.BOOL.optionalFieldOf("dry_bud", false).forGetter(DryingRecipe::dryBud)
            ).apply(inst, (ing, stack, time, dryBud) -> new DryingRecipe(id, ing, stack.orElse(ItemStack.EMPTY), time, dryBud)));
        }

        // Network (result optional -> writeItem handles empty stacks)
        @Override
        public DryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ing = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int time = buf.readVarInt();
            boolean dryBud = buf.readBoolean();
            return new DryingRecipe(id, ing, result, time, dryBud);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DryingRecipe recipe) {
            recipe.ingredient().toNetwork(buf);
            buf.writeItem(recipe.result());
            buf.writeVarInt(recipe.time());
            buf.writeBoolean(recipe.dryBud());
        }
    }
}
