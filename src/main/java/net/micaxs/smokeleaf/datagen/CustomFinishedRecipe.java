package net.micaxs.smokeleaf.datagen;

import com.google.gson.JsonObject;
import net.micaxs.smokeleaf.recipe.CodecRecipeSerializer;
import net.micaxs.smokeleaf.recipe.StrainCopyShapedRecipe;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Datagen wrapper that writes one of our recipe objects to JSON via its serializer
 * (1.20.1 datagen consumes {@link FinishedRecipe}s rather than recipe instances).
 */
public record CustomFinishedRecipe(ResourceLocation id, RecipeSerializer<?> serializer, JsonObject body) implements FinishedRecipe {

    @SuppressWarnings("unchecked")
    public static <R extends Recipe<?>> CustomFinishedRecipe of(R recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        JsonObject body;
        if (serializer instanceof CodecRecipeSerializer<?> codecSerializer) {
            body = ((CodecRecipeSerializer<R>) codecSerializer).toJson(recipe);
        } else if (serializer instanceof StrainCopyShapedRecipe.Serializer shaped && recipe instanceof StrainCopyShapedRecipe shapedRecipe) {
            body = shaped.toJson(shapedRecipe);
        } else {
            throw new IllegalArgumentException("No JSON writer for recipe serializer " + serializer);
        }
        return new CustomFinishedRecipe(recipe.getId(), serializer, body);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        for (Map.Entry<String, com.google.gson.JsonElement> entry : body.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return serializer;
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
        return null;
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
        return null;
    }
}
