package net.micaxs.smokeleaf.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * JSON codecs for recipe files. 1.20.1 has no {@code Ingredient.CODEC} and its
 * {@code ItemStack.CODEC} is the NBT shape ({@code id/Count/tag}), so recipe JSON goes through these.
 */
public final class CodecCompat {
    private CodecCompat() {}

    /** Converts any DynamicOps value to a JsonElement (identity for JsonOps). */
    public static <T> JsonElement toJson(DynamicOps<T> ops, T input) {
        return ops == JsonOps.INSTANCE ? (JsonElement) input : ops.convertTo(JsonOps.INSTANCE, input);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(DynamicOps<T> ops, JsonElement json) {
        return ops == JsonOps.INSTANCE ? (T) json : JsonOps.INSTANCE.convertTo(ops, json);
    }

    private static Codec<Ingredient> ingredient(boolean allowEmpty) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {
                try {
                    Ingredient ing = Ingredient.fromJson(toJson(ops, input), allowEmpty);
                    return DataResult.success(Pair.of(ing, ops.empty()));
                } catch (Exception e) {
                    return DataResult.error(() -> "Invalid ingredient: " + e.getMessage());
                }
            }

            @Override
            public <T> DataResult<T> encode(Ingredient input, DynamicOps<T> ops, T prefix) {
                return DataResult.success(fromJson(ops, input.toJson()));
            }
        };
    }

    /** Non-empty ingredient ({@code {"item":..}} / {@code {"tag":..}} / array). */
    public static final Codec<Ingredient> INGREDIENT = ingredient(false);
    /** Ingredient that may be empty. */
    public static final Codec<Ingredient> INGREDIENT_ALLOW_EMPTY = ingredient(true);

    /**
     * Result stack: {@code {"item"|"id": "<id>", "count": N, "nbt": {...} | "snbt"}}. Accepts the
     * 1.21 {@code id} key as well as the 1.20 {@code item} key, and always writes {@code item}.
     */
    public static final Codec<ItemStack> ITEM_STACK = new Codec<>() {
        @Override
        public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
            try {
                JsonElement el = toJson(ops, input);
                if (el.isJsonPrimitive()) {
                    Item item = item(el.getAsString());
                    return DataResult.success(Pair.of(new ItemStack(item), ops.empty()));
                }
                JsonObject obj = el.getAsJsonObject();
                String key = obj.has("item") ? "item" : "id";
                Item item = item(GsonHelper.getAsString(obj, key));
                int count = GsonHelper.getAsInt(obj, "count", 1);
                ItemStack stack = new ItemStack(item, count);
                if (obj.has("nbt")) {
                    JsonElement nbt = obj.get("nbt");
                    CompoundTag tag = nbt.isJsonObject()
                            ? TagParser.parseTag(nbt.toString())
                            : TagParser.parseTag(GsonHelper.convertToString(nbt, "nbt"));
                    stack.setTag(tag);
                }
                return DataResult.success(Pair.of(stack, ops.empty()));
            } catch (CommandSyntaxException | RuntimeException e) {
                return DataResult.error(() -> "Invalid item stack: " + e.getMessage());
            }
        }

        @Override
        public <T> DataResult<T> encode(ItemStack input, DynamicOps<T> ops, T prefix) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", BuiltInRegistries.ITEM.getKey(input.getItem()).toString());
            if (input.getCount() != 1) obj.addProperty("count", input.getCount());
            if (input.hasTag()) {
                // 1.20.1 stamps Damage:0 on every new damageable stack; that is not recipe data.
                CompoundTag tag = input.getTag().copy();
                if (tag.getInt("Damage") == 0) tag.remove("Damage");
                if (!tag.isEmpty()) obj.addProperty("nbt", tag.toString());
            }
            return DataResult.success(fromJson(ops, obj));
        }
    };

    private static Item item(String id) {
        ResourceLocation rl = new ResourceLocation(id);
        Item item = BuiltInRegistries.ITEM.getOptional(rl)
                .orElseThrow(() -> new IllegalArgumentException("Unknown item '" + id + "'"));
        if (item == Items.AIR) throw new IllegalArgumentException("Empty result item");
        return item;
    }

    /** Fluid stack JSON: {@code {"fluid": "<id>", "amount": N}}. */
    public static final MapCodec<FluidStack> FLUID_STACK_MAP = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount)
    ).apply(inst, (Fluid fluid, Integer amt) -> new FluidStack(fluid, amt)));
}
