package net.micaxs.smokeleaf.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Vanilla shaped crafting that copies STRAIN_DATA from the first ingredient carrying it to the
 * result. JSON is the vanilla shaped format with type {@code smokeleafindustries:strain_copy_shaped}.
 */
public class StrainCopyShapedRecipe extends ShapedRecipe {

    // Only set for recipes built in datagen, so they can be written back out as JSON.
    @Nullable private final Map<Character, Ingredient> key;
    @Nullable private final List<String> pattern;

    public StrainCopyShapedRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height,
                                  NonNullList<Ingredient> ingredients, ItemStack result, boolean showNotification) {
        super(id, group, category, width, height, ingredients, result, showNotification);
        this.key = null;
        this.pattern = null;
    }

    /** Datagen constructor: builds the ingredient grid from a key map + pattern rows. */
    public StrainCopyShapedRecipe(ResourceLocation id, String group, CraftingBookCategory category,
                                  Map<Character, Ingredient> key, List<String> pattern, ItemStack result, boolean showNotification) {
        super(id, group, category, pattern.get(0).length(), pattern.size(), dissolve(key, pattern), result, showNotification);
        this.key = key;
        this.pattern = pattern;
    }

    private static NonNullList<Ingredient> dissolve(Map<Character, Ingredient> key, List<String> pattern) {
        int width = pattern.get(0).length();
        NonNullList<Ingredient> list = NonNullList.withSize(width * pattern.size(), Ingredient.EMPTY);
        for (int row = 0; row < pattern.size(); row++) {
            String line = pattern.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                list.set(col + width * row, c == ' ' ? Ingredient.EMPTY : key.get(c));
            }
        }
        return list;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack crafted = getResultItem(registries).copy();
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            StrainData sd = ModDataComponentTypes.STRAIN_DATA.get(stack);
            if (sd != null) {
                ModDataComponentTypes.STRAIN_DATA.set(crafted, sd);
                break;
            }
        }
        return crafted;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRAIN_COPY_SHAPED_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<StrainCopyShapedRecipe> {

        private static StrainCopyShapedRecipe wrap(ShapedRecipe base) {
            return new StrainCopyShapedRecipe(base.getId(), base.getGroup(), base.category(),
                    base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem(null),
                    base.showNotification());
        }

        @Override
        public StrainCopyShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            return wrap(RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public @Nullable StrainCopyShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buf);
            return base == null ? null : wrap(base);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, StrainCopyShapedRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buf, recipe);
        }

        /** JSON body for datagen (without the {@code type} key). */
        public JsonObject toJson(StrainCopyShapedRecipe recipe) {
            if (recipe.key == null || recipe.pattern == null) {
                throw new IllegalStateException("Recipe " + recipe.getId() + " was not built for datagen");
            }
            JsonObject json = new JsonObject();
            if (!recipe.getGroup().isEmpty()) json.addProperty("group", recipe.getGroup());
            json.addProperty("category", recipe.category().getSerializedName());
            JsonObject keyJson = new JsonObject();
            recipe.key.forEach((c, ing) -> keyJson.add(String.valueOf(c), ing.toJson()));
            json.add("key", keyJson);
            JsonArray patternJson = new JsonArray();
            recipe.pattern.forEach(patternJson::add);
            json.add("pattern", patternJson);
            json.add("result", CodecCompat.ITEM_STACK.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,
                    recipe.getResultItem(null)).getOrThrow(false, s -> {}));
            json.addProperty("show_notification", recipe.showNotification());
            return json;
        }
    }
}
