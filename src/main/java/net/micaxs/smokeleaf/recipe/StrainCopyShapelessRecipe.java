package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Like vanilla shapeless crafting but copies STRAIN_DATA from the first
 * ingredient that carries it to the result. Used for bag ↔ weed recipes.
 */
public class StrainCopyShapelessRecipe extends ShapelessRecipe {

    private final ItemStack result;

    public StrainCopyShapelessRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, category, result, ingredients);
        this.result = result;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack crafted = result.copy();
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            StrainData sd = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (sd != null) {
                ModDataComponentTypes.STRAIN_DATA.set(crafted, sd);
                // Also carry the strain ID so bag↔weed conversions preserve full lineage.
                String strainId = ModDataComponentTypes.STRAIN_ID.get(stack);
                if (strainId != null && !strainId.isBlank()) {
                    ModDataComponentTypes.STRAIN_ID.set(crafted, strainId);
                }
                // Carry the discoverer name for "Discovered by" tooltip.
                String creator = ModDataComponentTypes.STRAIN_CREATOR.get(stack);
                if (creator != null && !creator.isBlank()) {
                    ModDataComponentTypes.STRAIN_CREATOR.set(crafted, creator);
                }
                break;
            }
        }
        return crafted;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRAIN_COPY_SHAPELESS_SERIALIZER.get();
    }

    public ItemStack result() {
        return result;
    }

    static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC = CodecCompat.INGREDIENT.listOf().flatXmap(
            ingredients -> {
                Ingredient[] values = ingredients.toArray(Ingredient[]::new);
                if (values.length == 0) {
                    return DataResult.error(() -> "No ingredients for shapeless recipe");
                }
                if (values.length > 9) {
                    return DataResult.error(() -> "Too many ingredients for shapeless recipe");
                }
                return DataResult.success(NonNullList.of(Ingredient.EMPTY, values));
            },
            DataResult::success
    );

    public static class Serializer implements CodecRecipeSerializer<StrainCopyShapelessRecipe> {

        @Override
        public MapCodec<StrainCopyShapelessRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
                    CodecCompat.ITEM_STACK.fieldOf("result").forGetter(StrainCopyShapelessRecipe::result),
                    INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(ShapelessRecipe::getIngredients)
            ).apply(instance, (group, category, result, ingredients) ->
                    new StrainCopyShapelessRecipe(id, group, category, result, ingredients)));
        }

        @Override
        public StrainCopyShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            ItemStack result = buf.readItem();
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < size; i++) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }
            return new StrainCopyShapelessRecipe(id, group, category, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, StrainCopyShapelessRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
        }
    }
}
