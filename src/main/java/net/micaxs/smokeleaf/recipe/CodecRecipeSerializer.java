package net.micaxs.smokeleaf.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Recipe serializer whose JSON form is described by a {@link MapCodec} (the 1.21 style), bound to
 * the recipe id. Network encoding stays explicit per recipe.
 */
public interface CodecRecipeSerializer<R extends Recipe<?>> extends RecipeSerializer<R> {

    /** Codec for the recipe JSON body; decoded recipes get {@code id}. */
    MapCodec<R> codec(ResourceLocation id);

    @Override
    default R fromJson(ResourceLocation id, JsonObject json) {
        return codec(id).codec().parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, msg -> {
                    throw new JsonParseException("Failed to parse recipe " + id + ": " + msg);
                });
    }

    /** JSON body for datagen (without the {@code type} key). */
    default JsonObject toJson(R recipe) {
        JsonElement el = codec(recipe.getId()).codec().encodeStart(JsonOps.INSTANCE, recipe)
                .getOrThrow(false, msg -> {
                    throw new IllegalStateException("Failed to encode recipe " + recipe.getId() + ": " + msg);
                });
        return el.getAsJsonObject();
    }
}
