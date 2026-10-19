package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
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
import net.minecraft.resources.Identifier;
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
public record GummyRecipe(Ingredient mold, IngredientWithCount catalyst, FluidStackTemplate oilTemplate, ItemStackTemplate outputTemplate) implements ModMachineRecipe<GummyRecipeInput> {

    /** Fresh copy of the oil (stored as a template: stacks can't be created while recipes load). */
    public FluidStack oil() {
        return oilTemplate.create();
    }

    /** Fresh copy of the output (stored as a template: stacks can't be created while recipes load). */
    public ItemStack output() {
        return outputTemplate.create();
    }


    @Override
    public boolean matches(GummyRecipeInput input, Level level) {
        return mold.test(input.getItem(0))
                && catalyst.ingredient().test(input.getItem(1))
                && input.getItem(1).getCount() >= catalyst.count();
    }

    @Override
    public ItemStack assemble(GummyRecipeInput input) {
        return output();
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(mold);
        list.add(catalyst.ingredient());
        return list;
    }

    @Override
    public RecipeSerializer<GummyRecipe> getSerializer() {
        return ModRecipes.GUMMY_SERIALIZER.get();
    }

    @Override
    public RecipeType<GummyRecipe> getType() {
        return ModRecipes.GUMMY_TYPE.get();
    }

    public static class Serializer {

        public static final MapCodec<FluidStackTemplate> FLUID_STACK_OBJECT = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(t -> t.fluid().value()),
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStackTemplate::amount)
        ).apply(inst, (Fluid fluid, Integer amt) -> new FluidStackTemplate(fluid, amt)));

        // JSON: {"item":"<id>", "count": N?}
        public static final MapCodec<ItemStackTemplate> OUTPUT_STACK_JSON = new MapCodec<>() {
            @Override
            public <T> DataResult<ItemStackTemplate> decode(DynamicOps<T> ops, MapLike<T> input) {
                T itemElem = input.get("item");
                if (itemElem == null) return DataResult.error(() -> "Missing output key \"item\"");
                DataResult<Identifier> id = Identifier.CODEC.parse(ops, itemElem);
                int count;
                T countElem = input.get("count");
                if (countElem != null) {
                    DataResult<Integer> c = Codec.INT.parse(ops, countElem);
                    count = c.result().orElse(1);
                } else {
                    count = 1;
                }
                return id.map(rl -> new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(rl), Math.max(1, count)));
            }

            @Override
            public <T> com.mojang.serialization.RecordBuilder<T> encode(ItemStackTemplate value, DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> builder) {
                Identifier rl = BuiltInRegistries.ITEM.getKey(value.item().value());
                builder.add(ops.createString("item"), Identifier.CODEC.encodeStart(ops, rl));
                if (value.count() > 1) {
                    builder.add(ops.createString("count"), Codec.INT.encodeStart(ops, value.count()));
                }
                return builder;
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString("item"), ops.createString("count"));
            }
        };

        public static final MapCodec<GummyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("mold").forGetter(GummyRecipe::mold),
                IngredientWithCount.CODEC.fieldOf("catalyst").forGetter(GummyRecipe::catalyst),
                FLUID_STACK_OBJECT.fieldOf("oil").forGetter(GummyRecipe::oilTemplate),
                OUTPUT_STACK_JSON.fieldOf("output").forGetter(GummyRecipe::outputTemplate)
        ).apply(inst, GummyRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidStackTemplate> FLUID_STACK_STREAM_CODEC = FluidStackTemplate.STREAM_CODEC;

        public static final StreamCodec<RegistryFriendlyByteBuf, GummyRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, GummyRecipe::mold,
                        IngredientWithCount.STREAM_CODEC, GummyRecipe::catalyst,
                        FLUID_STACK_STREAM_CODEC, GummyRecipe::oilTemplate,
                        ItemStackTemplate.STREAM_CODEC, GummyRecipe::outputTemplate,
                        GummyRecipe::new
                );

        public MapCodec<GummyRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, GummyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
