package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.stream.Stream;

/**
 * Mold + catalyst (sugar) + oil -> gummy. The mold and catalyst ingredients are checked in
 * {@link #matches}; the oil fluid type/amount is validated in the block entity's tick loop,
 * matching every other fluid-consuming machine recipe in this mod (Liquifier, Mutator).
 */
public record GummyRecipe(Ingredient mold, IngredientWithCount catalyst, FluidStack oil, ItemStack output) implements Recipe<GummyRecipeInput> {

    @Override
    public boolean matches(GummyRecipeInput input, Level level) {
        return mold.test(input.getItem(0))
                && catalyst.ingredient().test(input.getItem(1))
                && input.getItem(1).getCount() >= catalyst.count();
    }

    @Override
    public ItemStack assemble(GummyRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(mold);
        list.add(catalyst.ingredient());
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GUMMY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GUMMY_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GummyRecipe> {

        private static final MapCodec<FluidStack> FLUID_STACK_OBJECT = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
                Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount)
        ).apply(inst, (Fluid fluid, Integer amt) -> new FluidStack(fluid, amt)));

        // JSON: {"item":"<id>", "count": N?}
        private static final MapCodec<ItemStack> OUTPUT_STACK_JSON = new MapCodec<>() {
            @Override
            public <T> DataResult<ItemStack> decode(DynamicOps<T> ops, MapLike<T> input) {
                T itemElem = input.get("item");
                if (itemElem == null) return DataResult.error(() -> "Missing output key \"item\"");
                DataResult<ResourceLocation> id = ResourceLocation.CODEC.parse(ops, itemElem);
                int count;
                T countElem = input.get("count");
                if (countElem != null) {
                    DataResult<Integer> c = Codec.INT.parse(ops, countElem);
                    count = c.result().orElse(1);
                } else {
                    count = 1;
                }
                return id.map(rl -> {
                    Item it = BuiltInRegistries.ITEM.get(rl);
                    return new ItemStack(it, Math.max(1, count));
                });
            }

            @Override
            public <T> com.mojang.serialization.RecordBuilder<T> encode(ItemStack value, DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> builder) {
                ResourceLocation rl = BuiltInRegistries.ITEM.getKey(value.getItem());
                builder.add(ops.createString("item"), ResourceLocation.CODEC.encodeStart(ops, rl));
                if (value.getCount() > 1) {
                    builder.add(ops.createString("count"), Codec.INT.encodeStart(ops, value.getCount()));
                }
                return builder;
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString("item"), ops.createString("count"));
            }
        };

        public static final MapCodec<GummyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("mold").forGetter(GummyRecipe::mold),
                IngredientWithCount.CODEC.fieldOf("catalyst").forGetter(GummyRecipe::catalyst),
                FLUID_STACK_OBJECT.fieldOf("oil").forGetter(GummyRecipe::oil),
                OUTPUT_STACK_JSON.fieldOf("output").forGetter(GummyRecipe::output)
        ).apply(inst, GummyRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> FLUID_STACK_STREAM_CODEC =
                StreamCodec.of(
                        (buf, stack) -> {
                            ByteBufCodecs.idMapper(BuiltInRegistries.FLUID).encode(buf, stack.getFluid());
                            buf.writeVarInt(stack.getAmount());
                        },
                        buf -> {
                            Fluid f = ByteBufCodecs.idMapper(BuiltInRegistries.FLUID).decode(buf);
                            int amt = buf.readVarInt();
                            return new FluidStack(f, amt);
                        }
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, GummyRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, GummyRecipe::mold,
                        IngredientWithCount.STREAM_CODEC, GummyRecipe::catalyst,
                        FLUID_STACK_STREAM_CODEC, GummyRecipe::oil,
                        ItemStack.STREAM_CODEC, GummyRecipe::output,
                        GummyRecipe::new
                );

        @Override
        public MapCodec<GummyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GummyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
