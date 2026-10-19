package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/** Like vanilla shaped crafting but copies STRAIN_DATA from the first ingredient that carries it to the result. */
public class StrainCopyShapedRecipe extends ShapedRecipe {

    private final ItemStackTemplate result;

    public StrainCopyShapedRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
        this.result = result;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack crafted = super.assemble(input);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            StrainData sd = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (sd != null) {
                crafted.set(ModDataComponentTypes.STRAIN_DATA.get(), sd);
                break;
            }
        }
        return crafted;
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        @SuppressWarnings("unchecked")
        RecipeSerializer<ShapedRecipe> serializer = (RecipeSerializer<ShapedRecipe>) (RecipeSerializer<?>) ModRecipes.STRAIN_COPY_SHAPED_SERIALIZER.get();
        return serializer;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public static class Serializer {
        public static final MapCodec<StrainCopyShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
                ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pattern),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(StrainCopyShapedRecipe::result)
        ).apply(instance, StrainCopyShapedRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapedRecipe> STREAM_CODEC = StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
                CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
                ShapedRecipePattern.STREAM_CODEC, r -> r.pattern,
                ItemStackTemplate.STREAM_CODEC, StrainCopyShapedRecipe::result,
                StrainCopyShapedRecipe::new
        );
    }
}
