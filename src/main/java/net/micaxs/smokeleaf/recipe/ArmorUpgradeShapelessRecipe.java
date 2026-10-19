package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

/**
 * Like vanilla shapeless crafting, but the first ingredient matching {@code baseIngredient} is
 * transmuted into the result item rather than being consumed for a from-scratch result — so
 * enchantments, trims, custom names and durability all carry over, the same way the vanilla
 * netherite smithing upgrade preserves them. Used for Baja Hoodie -&gt; Reinforced Baja Hoodie.
 */
public class ArmorUpgradeShapelessRecipe extends ShapelessRecipe {

    private final Ingredient baseIngredient;
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;

    public ArmorUpgradeShapelessRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, Ingredient baseIngredient,
                                        ItemStackTemplate result, List<Ingredient> ingredients) {
        super(commonInfo, bookInfo, result, ingredients);
        this.baseIngredient = baseIngredient;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (baseIngredient.test(stack)) {
                ItemStack upgraded = stack.transmuteCopy(result.item().value(), result.count());
                upgraded.applyComponents(result.components());
                return upgraded;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        @SuppressWarnings("unchecked")
        RecipeSerializer<ShapelessRecipe> serializer = (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) ModRecipes.ARMOR_UPGRADE_SHAPELESS_SERIALIZER.get();
        return serializer;
    }

    public Ingredient baseIngredient() {
        return baseIngredient;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public static class Serializer {
        public static final MapCodec<CraftingRecipe.CraftingBookInfo> BOOK_INFO_CODEC =
                Recipe.BookInfo.mapCodec(CraftingBookCategory.CODEC, CraftingBookCategory.EQUIPMENT, CraftingRecipe.CraftingBookInfo::new);

        public static final MapCodec<ArmorUpgradeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
                BOOK_INFO_CODEC.forGetter(r -> r.bookInfo),
                Ingredient.CODEC.fieldOf("base").forGetter(ArmorUpgradeShapelessRecipe::baseIngredient),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(ArmorUpgradeShapelessRecipe::result),
                Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(ArmorUpgradeShapelessRecipe::ingredients)
        ).apply(instance, ArmorUpgradeShapelessRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ArmorUpgradeShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
                CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
                Ingredient.CONTENTS_STREAM_CODEC, ArmorUpgradeShapelessRecipe::baseIngredient,
                ItemStackTemplate.STREAM_CODEC, ArmorUpgradeShapelessRecipe::result,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ArmorUpgradeShapelessRecipe::ingredients,
                ArmorUpgradeShapelessRecipe::new
        );
    }
}
