package net.micaxs.smokeleaf.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientWithCount(Ingredient ingredient, int count) {
    // Decodes entries like { "item":"...", "count":N } or { "tag":"...", "count":N }
    public static final Codec<IngredientWithCount> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<IngredientWithCount, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Ingredient> ingr = CodecCompat.INGREDIENT.parse(ops, input);
            final int count = readCount(ops, input);
            return ingr.map(i -> Pair.of(new IngredientWithCount(i, Math.max(1, count)), input));
        }

        private <T> int readCount(DynamicOps<T> ops, T input) {
            int c = 1;
            var map = ops.getMap(input);
            if (map.result().isPresent()) {
                var m = map.result().get();
                T countElem = m.get("count");
                if (countElem != null) {
                    var parsed = Codec.INT.parse(ops, countElem);
                    if (parsed.result().isPresent()) {
                        c = parsed.result().get();
                    }
                }
            }
            return c;
        }

        @Override
        public <T> DataResult<T> encode(IngredientWithCount value, DynamicOps<T> ops, T prefix) {
            var base = CodecCompat.INGREDIENT.encodeStart(ops, value.ingredient());
            if (base.result().isPresent()) {
                T enc = base.result().get();
                var asMap = ops.getMap(enc);
                if (asMap.result().isPresent()) {
                    var builder = ops.mapBuilder();
                    var m = asMap.result().get();
                    m.entries().forEach(p -> builder.add(p.getFirst(), p.getSecond()));
                    if (value.count() > 1) {
                        builder.add(ops.createString("count"), Codec.INT.encodeStart(ops, value.count()));
                    }
                    return builder.build(prefix);
                }
                // Fallback: wrap as {"ingredient": <encoded>, "count": N}
                var builder = ops.mapBuilder();
                builder.add(ops.createString("ingredient"), enc);
                if (value.count() > 1) {
                    builder.add(ops.createString("count"), Codec.INT.encodeStart(ops, value.count()));
                }
                return builder.build(prefix);
            }
            return DataResult.error(() -> "Failed to encode ingredient");
        }
    };

    public static IngredientWithCount fromNetwork(FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int count = buf.readVarInt();
        return new IngredientWithCount(ingredient, count);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        buf.writeVarInt(count);
    }

    // Builds a display ingredient that carries the required count in getItems()[0].getCount()
    public Ingredient asDisplayIngredient() {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return ingredient; // nothing to show
        ItemStack[] counted = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            counted[i] = items[i].copyWithCount(count);
        }
        return Ingredient.of(counted);
    }
}