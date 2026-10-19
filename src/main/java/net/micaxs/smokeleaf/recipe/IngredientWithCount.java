package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient ingredient, int count) {
    // Entries look like { "ingredient": "<item id>" | "#<tag>", "count": N } (count defaults to 1).
    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
    ).apply(inst, IngredientWithCount::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, IngredientWithCount::ingredient,
                    ByteBufCodecs.VAR_INT, IngredientWithCount::count,
                    IngredientWithCount::new
            );

    /** The accepted stacks, each carrying the required count (for JEI/guide display). */
    public java.util.List<ItemStack> displayStacks() {
        java.util.List<ItemStack> counted = new java.util.ArrayList<>();
        for (ItemStack item : IngredientUtil.stacks(ingredient)) {
            counted.add(item.copyWithCount(count));
        }
        return counted;
    }
}
