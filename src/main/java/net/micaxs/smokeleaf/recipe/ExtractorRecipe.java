package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ExtractorRecipe(Ingredient inputItem, ItemStackTemplate outputTemplate) implements ModMachineRecipe<ExtractorRecipeInput> {

    /** Fresh copy of the output (stored as a template: stacks can't be created while recipes load). */
    public ItemStack output() {
        return outputTemplate.create();
    }


    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ExtractorRecipeInput extractorRecipeInput, Level level) {
        return inputItem.test(extractorRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ExtractorRecipeInput extractorRecipeInput) {
        ItemStack out = output().copy();
        ItemStack in = extractorRecipeInput.getItem(0);
        if (!in.isEmpty()) {
            StrainData sd = in.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (sd != null) {
                out.set(ModDataComponentTypes.STRAIN_DATA.get(), sd);
            } else {
                Integer thc = in.get(ModDataComponentTypes.THC.get());
                Integer cbd = in.get(ModDataComponentTypes.CBD.get());
                if (thc != null) out.set(ModDataComponentTypes.THC.get(), thc);
                if (cbd != null) out.set(ModDataComponentTypes.CBD.get(), cbd);
            }
        }
        return out;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output();
    }

    @Override
    public RecipeSerializer<ExtractorRecipe> getSerializer() {
        return ModRecipes.EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<ExtractorRecipe> getType() {
        return ModRecipes.EXTRACTOR_TYPE.get();
    }


    public static class Serializer {
        // Codec
        public static final MapCodec<ExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ExtractorRecipe::inputItem),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(ExtractorRecipe::outputTemplate)
        ).apply(inst, ExtractorRecipe::new));

        // StreamCodec
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ExtractorRecipe::inputItem,
                        ItemStackTemplate.STREAM_CODEC, ExtractorRecipe::outputTemplate,
                        ExtractorRecipe::new);


        public MapCodec<ExtractorRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
