package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
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

public record ManualGrinderRecipe(Ingredient ingredient, ItemStackTemplate resultTemplate, int grindTime) implements ModMachineRecipe<ManualGrinderInput> {

    /** Fresh copy of the result (stored as a template: stacks can't be created while recipes load). */
    public ItemStack result() {
        return resultTemplate.create();
    }


    @Override
    public boolean matches(ManualGrinderInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ManualGrinderInput input) {
        ItemStack out = result().copy();
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
            // Propagate strain lineage components
            String strainId = in.get(ModDataComponentTypes.STRAIN_ID.get());
            if (strainId != null) out.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
            String strainCreator = in.get(ModDataComponentTypes.STRAIN_CREATOR.get());
            if (strainCreator != null) out.set(ModDataComponentTypes.STRAIN_CREATOR.get(), strainCreator);
        }

        return out;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result().copy();
    }

    @Override
    public RecipeSerializer<ManualGrinderRecipe> getSerializer() {
        return ModRecipes.MANUAL_GRINDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<ManualGrinderRecipe> getType() {
        return ModRecipes.MANUAL_GRINDER_TYPE.get();
    }

    public static class Serializer {
        public static final MapCodec<ManualGrinderRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ManualGrinderRecipe::ingredient),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(ManualGrinderRecipe::resultTemplate),
                Codec.INT.optionalFieldOf("grind_time", 40).forGetter(ManualGrinderRecipe::grindTime)
        ).apply(inst, ManualGrinderRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ManualGrinderRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ManualGrinderRecipe::ingredient,
                        ItemStackTemplate.STREAM_CODEC, ManualGrinderRecipe::resultTemplate,
                        ByteBufCodecs.VAR_INT, ManualGrinderRecipe::grindTime,
                        ManualGrinderRecipe::new
                );

        public MapCodec<ManualGrinderRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ManualGrinderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
