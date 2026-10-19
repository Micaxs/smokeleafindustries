package net.micaxs.smokeleaf.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.stream.Stream;

public record MutatorRecipe(NonNullList<IngredientWithCount> inputItems, FluidStackTemplate fluidTemplate, ItemStackTemplate outputTemplate)
        implements ModMachineRecipe<MutatorRecipeInput> {

    /** Fresh copy of the required fluid (stored as a template: stacks can't be created while recipes load). */
    public FluidStack getFluid() {
        return fluidTemplate.create();
    }

    /** Fresh copy of the output. */
    public ItemStack output() {
        return outputTemplate.create();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (IngredientWithCount iwc : inputItems) {
            list.add(iwc.ingredient());
        }
        return list;
    }

    @Override
    public boolean matches(MutatorRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        if (inputItems.size() != 2) return false;

        IngredientWithCount a = inputItems.get(0);
        IngredientWithCount b = inputItems.get(1);

        ItemStack seed = input.seedInput();
        ItemStack extract = input.extractInput();

        if (!a.ingredient().test(seed) || seed.getCount() < a.count()) return false;
        if (!b.ingredient().test(extract) || extract.getCount() < b.count()) return false;

        // Fluid Check is done in BlockEntity for now..

        return true;
    }

    @Override
    public ItemStack assemble(MutatorRecipeInput input) {
        return output();
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output();
    }

    @Override
    public RecipeSerializer<MutatorRecipe> getSerializer() {
        return ModRecipes.MUTATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<MutatorRecipe> getType() {
        return ModRecipes.MUTATOR_TYPE.get();
    }

    // JSON: {"FluidName":"<id>", "Amount": N}
    public static final MapCodec<FluidStackTemplate> FLUID_STACK_JSON = new MapCodec<>() {
        @Override
        public <T> DataResult<FluidStackTemplate> decode(DynamicOps<T> ops, MapLike<T> input) {
            T idElem = input.get("FluidName");
            T amtElem = input.get("Amount");
            if (idElem == null || amtElem == null) {
                return DataResult.error(() -> "Missing fluid keys \"FluidName\" and/or \"Amount\"");
            }
            DataResult<Identifier> id = Identifier.CODEC.parse(ops, idElem);
            DataResult<Integer> amt = Codec.INT.parse(ops, amtElem);
            return id.apply2((rl, a) -> new FluidStackTemplate(BuiltInRegistries.FLUID.getValue(rl), Math.max(1, a)), amt);
        }

        @Override
        public <T> RecordBuilder<T> encode(FluidStackTemplate value, DynamicOps<T> ops, RecordBuilder<T> builder) {
            Identifier rl = BuiltInRegistries.FLUID.getKey(value.fluid().value());
            builder.add(ops.createString("FluidName"), Identifier.CODEC.encodeStart(ops, rl));
            builder.add(ops.createString("Amount"), Codec.INT.encodeStart(ops, value.amount()));
            return builder;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("FluidName"), ops.createString("Amount"));
        }
    };

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
                if (c.result().isPresent()) count = c.result().get();
                else {
                    count = 1;
                }
            } else {
                count = 1;
            }
            return id.map(rl -> new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(rl), Math.max(1, count)));
        }

        @Override
        public <T> RecordBuilder<T> encode(ItemStackTemplate value, DynamicOps<T> ops, RecordBuilder<T> builder) {
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

    public static class Serializer {
        public static final MapCodec<MutatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IngredientWithCount.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.inputItems),
                FLUID_STACK_JSON.fieldOf("fluid").forGetter(r -> r.fluidTemplate),
                OUTPUT_STACK_JSON.fieldOf("output").forGetter(r -> r.outputTemplate)
        ).apply(instance, (ingredients, fluid, output) -> {
            NonNullList<IngredientWithCount> list = NonNullList.create();
            list.addAll(ingredients);
            return new MutatorRecipe(list, fluid, output);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, MutatorRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public MutatorRecipe decode(RegistryFriendlyByteBuf buf) {
                        int size = buf.readVarInt();
                        NonNullList<IngredientWithCount> ings = NonNullList.create();
                        for (int i = 0; i < size; i++) {
                            ings.add(IngredientWithCount.STREAM_CODEC.decode(buf));
                        }
                        FluidStackTemplate fluid = FluidStackTemplate.STREAM_CODEC.decode(buf);
                        ItemStackTemplate out = ItemStackTemplate.STREAM_CODEC.decode(buf);
                        return new MutatorRecipe(ings, fluid, out);
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, MutatorRecipe value) {
                        buf.writeVarInt(value.inputItems.size());
                        for (IngredientWithCount iwc : value.inputItems) {
                            IngredientWithCount.STREAM_CODEC.encode(buf, iwc);
                        }
                        FluidStackTemplate.STREAM_CODEC.encode(buf, value.fluidTemplate);
                        ItemStackTemplate.STREAM_CODEC.encode(buf, value.outputTemplate);
                    }
                };

        public MapCodec<MutatorRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, MutatorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}