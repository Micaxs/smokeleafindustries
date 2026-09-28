package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import java.util.stream.Stream;

public record MutatorRecipe(ResourceLocation id, NonNullList<IngredientWithCount> inputItems, FluidStack fluid, ItemStack output)
        implements Recipe<MutatorRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
    }


    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (IngredientWithCount iwc : inputItems) {
            list.add(iwc.asDisplayIngredient());
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
    public ItemStack assemble(MutatorRecipeInput input, RegistryAccess provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MUTATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MUTATOR_TYPE.get();
    }

    // JSON: {"FluidName":"<id>", "Amount": N}
    private static final MapCodec<FluidStack> FLUID_STACK_JSON = new MapCodec<>() {
        @Override
        public <T> DataResult<FluidStack> decode(DynamicOps<T> ops, MapLike<T> input) {
            T idElem = input.get("FluidName");
            T amtElem = input.get("Amount");
            if (idElem == null || amtElem == null) {
                return DataResult.error(() -> "Missing fluid keys \"FluidName\" and/or \"Amount\"");
            }
            DataResult<ResourceLocation> id = ResourceLocation.CODEC.parse(ops, idElem);
            DataResult<Integer> amt = Codec.INT.parse(ops, amtElem);
            return id.apply2((rl, a) -> new FluidStack(BuiltInRegistries.FLUID.get(rl), Math.max(0, a)), amt);
        }

        @Override
        public <T> RecordBuilder<T> encode(FluidStack value, DynamicOps<T> ops, RecordBuilder<T> builder) {
            ResourceLocation rl = BuiltInRegistries.FLUID.getKey(value.getFluid());
            builder.add(ops.createString("FluidName"), ResourceLocation.CODEC.encodeStart(ops, rl));
            builder.add(ops.createString("Amount"), Codec.INT.encodeStart(ops, value.getAmount()));
            return builder;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("FluidName"), ops.createString("Amount"));
        }
    };

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
                if (c.result().isPresent()) count = c.result().get();
                else {
                    count = 1;
                }
            } else {
                count = 1;
            }
            return id.map(rl -> {
                Item it = BuiltInRegistries.ITEM.get(rl);
                return new ItemStack(it, Math.max(1, count));
            });
        }

        @Override
        public <T> RecordBuilder<T> encode(ItemStack value, DynamicOps<T> ops, RecordBuilder<T> builder) {
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

    public static class Serializer implements CodecRecipeSerializer<MutatorRecipe> {
        @Override
        public MapCodec<MutatorRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IngredientWithCount.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.inputItems),
                    FLUID_STACK_JSON.fieldOf("fluid").forGetter(r -> r.fluid),
                    OUTPUT_STACK_JSON.fieldOf("output").forGetter(r -> r.output)
            ).apply(instance, (ingredients, fluid, output) -> {
                NonNullList<IngredientWithCount> list = NonNullList.create();
                list.addAll(ingredients);
                return new MutatorRecipe(id, list, fluid, output);
            }));
        }

        @Override
        public MutatorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<IngredientWithCount> ings = NonNullList.create();
            for (int i = 0; i < size; i++) {
                ings.add(IngredientWithCount.fromNetwork(buf));
            }
            FluidStack fluid = FluidStack.readFromPacket(buf);
            ItemStack out = buf.readItem();
            return new MutatorRecipe(id, ings, fluid, out);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MutatorRecipe value) {
            buf.writeVarInt(value.inputItems.size());
            for (IngredientWithCount iwc : value.inputItems) {
                iwc.toNetwork(buf);
            }
            value.fluid.writeToPacket(buf);
            buf.writeItem(value.output);
        }
    }
}
