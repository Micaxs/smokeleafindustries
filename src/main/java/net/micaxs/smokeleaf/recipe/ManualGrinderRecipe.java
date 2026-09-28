package net.micaxs.smokeleaf.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ManualGrinderRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int grindTime) implements Recipe<ManualGrinderInput> {

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
    public boolean matches(ManualGrinderInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ManualGrinderInput input, RegistryAccess provider) {
        ItemStack out = result.copy();
        ItemStack in = input.getItem(0);

        if (!in.isEmpty()) {
            // If input carries StrainData, copy it to the output (primary path for generic items)
            net.micaxs.smokeleaf.strain.StrainData strainData =
                    ModDataComponentTypes.STRAIN_DATA.get(in);
            if (strainData != null) {
                ModDataComponentTypes.STRAIN_DATA.set(out, strainData);
            } else {
                // Legacy path: per-strain items without StrainData
                if (out.getItem() instanceof BaseWeedItem weedItem) {
                    weedItem.initializeStack(out);
                }
                Integer thc = ModDataComponentTypes.THC.get(in);
                Integer cbd = ModDataComponentTypes.CBD.get(in);
                if (thc != null) ModDataComponentTypes.THC.set(out, thc);
                if (cbd != null) ModDataComponentTypes.CBD.set(out, cbd);
            }
            // Propagate strain lineage components
            String strainId = ModDataComponentTypes.STRAIN_ID.get(in);
            if (strainId != null) ModDataComponentTypes.STRAIN_ID.set(out, strainId);
            String strainCreator = ModDataComponentTypes.STRAIN_CREATOR.get(in);
            if (strainCreator != null) ModDataComponentTypes.STRAIN_CREATOR.set(out, strainCreator);
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
    public ItemStack getResultItem(RegistryAccess provider) {
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

    public static class Serializer implements CodecRecipeSerializer<ManualGrinderRecipe> {
        @Override
        public MapCodec<ManualGrinderRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(ManualGrinderRecipe::ingredient),
                    CodecCompat.ITEM_STACK.fieldOf("result").forGetter(ManualGrinderRecipe::result),
                    Codec.INT.optionalFieldOf("grind_time", 40).forGetter(ManualGrinderRecipe::grindTime)
            ).apply(inst, (ing, result, time) -> new ManualGrinderRecipe(id, ing, result, time)));
        }

        @Override
        public ManualGrinderRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new ManualGrinderRecipe(id, Ingredient.fromNetwork(buf), buf.readItem(), buf.readVarInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ManualGrinderRecipe recipe) {
            recipe.ingredient().toNetwork(buf);
            buf.writeItem(recipe.result());
            buf.writeVarInt(recipe.grindTime());
        }
    }
}
