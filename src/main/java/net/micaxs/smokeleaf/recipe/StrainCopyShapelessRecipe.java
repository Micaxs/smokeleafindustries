package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

/**
 * Like vanilla shapeless crafting but copies STRAIN_DATA from the first
 * ingredient that carries it to the result. Used for bag ↔ weed recipes.
 */
public class StrainCopyShapelessRecipe extends ShapelessRecipe {

    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;

    public StrainCopyShapelessRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
        super(commonInfo, bookInfo, result, ingredients);
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack crafted = super.assemble(input);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            StrainData sd = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (sd != null) {
                crafted.set(ModDataComponentTypes.STRAIN_DATA.get(), sd);
                // Also carry the strain ID so bag↔weed conversions preserve full lineage.
                String strainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
                if (strainId != null && !strainId.isBlank()) {
                    crafted.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
                }
                // Carry the discoverer name for "Discovered by" tooltip.
                String creator = stack.get(ModDataComponentTypes.STRAIN_CREATOR.get());
                if (creator != null && !creator.isBlank()) {
                    crafted.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creator);
                }
                break;
            }
        }
        return crafted;
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        @SuppressWarnings("unchecked")
        RecipeSerializer<ShapelessRecipe> serializer = (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) ModRecipes.STRAIN_COPY_SHAPELESS_SERIALIZER.get();
        return serializer;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public static class Serializer {
        public static final MapCodec<StrainCopyShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(StrainCopyShapelessRecipe::result),
                Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(StrainCopyShapelessRecipe::ingredients)
        ).apply(instance, StrainCopyShapelessRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
                CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
                ItemStackTemplate.STREAM_CODEC, StrainCopyShapelessRecipe::result,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), StrainCopyShapelessRecipe::ingredients,
                StrainCopyShapelessRecipe::new
        );
    }
}
