package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
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

public record GrinderRecipe(Ingredient inputItem, ItemStack output) implements Recipe<GrinderRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(GrinderRecipeInput grinderRecipeInput, Level level) {
        return inputItem.test(grinderRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(GrinderRecipeInput grinderRecipeInput, HolderLookup.Provider provider) {
        ItemStack out = output.copy();
        ItemStack in = grinderRecipeInput.getItem(0);
        if (!in.isEmpty()) {
            StrainData sd = in.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (sd != null) {
                // STRAIN_DATA is the source of truth — skip legacy initializeStack to avoid
                // stamping individual thc/cbd/active_ingredient/effect_duration components.
                out.set(ModDataComponentTypes.STRAIN_DATA.get(), sd);
            } else {
                // Legacy path: initialize weed defaults then copy individual components.
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
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GRINDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GrinderRecipe> {
        public static final MapCodec<GrinderRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(GrinderRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(GrinderRecipe::output)
        ).apply(inst, GrinderRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GrinderRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, GrinderRecipe::inputItem,
                        ItemStack.STREAM_CODEC, GrinderRecipe::output,
                        GrinderRecipe::new);

        @Override
        public MapCodec<GrinderRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GrinderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
