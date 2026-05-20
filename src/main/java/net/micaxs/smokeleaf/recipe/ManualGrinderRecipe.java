package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.ModRecipes;
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

public record ManualGrinderRecipe(Ingredient ingredient, ItemStack result, int grindTime) implements Recipe<ManualGrinderInput> {

    @Override
    public boolean matches(ManualGrinderInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ManualGrinderInput input, HolderLookup.Provider provider) {
        ItemStack out = result.copy();
        ItemStack in = input.getItem(0);

        if (!in.isEmpty()) {
            // If input carries StrainData, copy it to the output (primary path for generic items)
            net.micaxs.smokeleaf.strain.StrainData strainData =
                    in.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (strainData != null) {
                out.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
            } else {
                // Legacy path: per-strain items without StrainData
                if (out.getItem() instanceof BaseWeedItem weedItem) {
                    weedItem.initializeStack(out);
                }
                Integer thc = in.get(ModDataComponentTypes.THC.get());
                Integer cbd = in.get(ModDataComponentTypes.CBD.get());
                if (thc != null) out.set(ModDataComponentTypes.THC.get(), thc);
                if (cbd != null) out.set(ModDataComponentTypes.CBD.get(), cbd);
            }
        }

        return out;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MANUAL_GRINDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MANUAL_GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ManualGrinderRecipe> {
        public static final MapCodec<ManualGrinderRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ManualGrinderRecipe::ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(ManualGrinderRecipe::result),
                Codec.INT.optionalFieldOf("grind_time", 40).forGetter(ManualGrinderRecipe::grindTime)
        ).apply(inst, ManualGrinderRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ManualGrinderRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ManualGrinderRecipe::ingredient,
                        ItemStack.STREAM_CODEC, ManualGrinderRecipe::result,
                        ByteBufCodecs.VAR_INT, ManualGrinderRecipe::grindTime,
                        ManualGrinderRecipe::new
                );

        @Override
        public MapCodec<ManualGrinderRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ManualGrinderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
