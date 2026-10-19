package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import com.mojang.serialization.Codec;
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

import java.util.Optional;

/**
 * Drying recipe:
 * - ingredient: input item (bud, leaf, etc.)
 * - result: produced item (may be empty if only drying in-place, e.g. bud dry flag)
 * - time: ticks needed (default 200)
 * - dryBud: if true, block entity will set bud dry flag instead of replacing stack
 */
public record DryingRecipe(Ingredient ingredient, Optional<ItemStackTemplate> resultTemplate, int time, boolean dryBud)
        implements ModMachineRecipe<DryingRecipeInput> {

    /** Fresh copy of the result (stored as a template: stacks can't be created while recipes load). */
    public ItemStack result() {
        return resultTemplate.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }


    @Override
    public boolean matches(DryingRecipeInput input, Level level) {
        //if (level.isClientSide()) return false;
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(DryingRecipeInput input) {
        return result();
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return ModRecipes.DRYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<DryingRecipe> getType() {
        return ModRecipes.DRYING_TYPE.get();
    }

    public static class Serializer {

        // JSON codec (result optional)
        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(DryingRecipe::ingredient),
                ItemStackTemplate.CODEC.optionalFieldOf("result").forGetter(DryingRecipe::resultTemplate),
                Codec.INT.optionalFieldOf("time", 200).forGetter(DryingRecipe::time),
                Codec.BOOL.optionalFieldOf("dry_bud", false).forGetter(DryingRecipe::dryBud)
        ).apply(inst, (ing, stack, time, dryBud) -> new DryingRecipe(ing, stack, time, dryBud)));

        // Network codec (result optional -> avoid encoding empty with non-empty codec)
        public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, DryingRecipe::ingredient,
                        ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), DryingRecipe::resultTemplate,
                        ByteBufCodecs.VAR_INT, DryingRecipe::time,
                        ByteBufCodecs.BOOL, DryingRecipe::dryBud,
                        (ing, optResult, time, dryBud) ->
                                new DryingRecipe(ing, optResult, time, dryBud)
                );

        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}