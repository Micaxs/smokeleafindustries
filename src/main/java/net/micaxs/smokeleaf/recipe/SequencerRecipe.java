package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.DNAContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SequencerRecipe(Ingredient dnaIngredient,
                              Ingredient baseExtractIngredient,
                              Ingredient[] requiredReagents,
                              ItemStack result,
                              Optional<String> strainId) implements Recipe<SequencerRecipeInput> {

    public static final int REAGENT_SLOTS = 3;

    @Override
    public boolean matches(SequencerRecipeInput input, Level level) {
        if (!(input.dna().getItem() instanceof DNAStrandItem)) {
            return false;
        }
        if (!dnaIngredient.test(input.dna())) {
            return false;
        }
        if (!baseExtractIngredient.test(input.baseExtract())) {
            return false;
        }

        if (!level.isClientSide()) {
            boolean hasComp = input.dna().has(ModDataComponentTypes.DNA_CONTENTS.get());
        }

        if (!level.isClientSide()) {
            boolean hasComp = input.dna().has(ModDataComponentTypes.DNA_CONTENTS.get());
            DNAContents raw = DNAStrandItem.getContents(input.dna());
            for (int i = 0; i < REAGENT_SLOTS; i++) {
                ItemStack s = raw.get(i);
            }
        }

        DNAContents contents = DNAStrandItem.getContents(input.dna());

        List<ItemStack> inside = new ArrayList<>(REAGENT_SLOTS);
        for (int i = 0; i < REAGENT_SLOTS; i++) {
            ItemStack stack = contents.get(i);
            if (stack.isEmpty()) {
                return false;
            }
            inside.add(stack);
        }

        boolean[] used = new boolean[inside.size()];
        for (int r = 0; r < requiredReagents.length; r++) {
            Ingredient required = requiredReagents[r];
            boolean found = false;
            for (int i = 0; i < inside.size(); i++) {
                if (!used[i] && required.test(inside.get(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(SequencerRecipeInput input, HolderLookup.Provider provider) {
        ItemStack out = result.copy();
        strainId.ifPresent(id -> {
            net.micaxs.smokeleaf.strain.StrainRegistry.get(id).ifPresent(strainData -> {
                out.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
                out.set(ModDataComponentTypes.STRAIN_ID.get(), id);
            });
        });
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack out = result.copy();
        strainId.ifPresent(id -> {
            net.micaxs.smokeleaf.strain.StrainRegistry.get(id).ifPresent(strainData -> {
                out.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
                out.set(ModDataComponentTypes.STRAIN_ID.get(), id);
            });
        });
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SEQUENCER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SEQUENCER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(dnaIngredient);
        list.add(baseExtractIngredient);
        list.addAll(List.of(requiredReagents));
        return list;
    }

    public static class Serializer implements RecipeSerializer<SequencerRecipe> {

        private static final Codec<ItemStack> RESULT_STACK_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(s -> s.getItemHolder().value()),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
                ).apply(inst, (item, count) -> new ItemStack(item, count)));

        private static final MapCodec<SequencerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("dna").forGetter(SequencerRecipe::dnaIngredient),
                        Ingredient.CODEC_NONEMPTY.fieldOf("base_extract").forGetter(SequencerRecipe::baseExtractIngredient),
                        Ingredient.CODEC.listOf().fieldOf("required_reagents")
                                .flatXmap(list -> list.size() == REAGENT_SLOTS
                                                ? DataResult.success(list)
                                                : DataResult.error(() -> "required_reagents must have exactly " + REAGENT_SLOTS),
                                        DataResult::success)
                                .forGetter(r -> java.util.List.of(r.requiredReagents)),
                        RESULT_STACK_CODEC.fieldOf("result").forGetter(SequencerRecipe::result),
                        Codec.STRING.optionalFieldOf("strain_id").forGetter(SequencerRecipe::strainId)
                ).apply(instance, (dna, base, reagentsList, result, strainId) ->
                        new SequencerRecipe(dna, base, reagentsList.toArray(Ingredient[]::new), result, strainId))
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, SequencerRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public SequencerRecipe decode(RegistryFriendlyByteBuf buf) {
                        Ingredient dna = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        Ingredient[] req = new Ingredient[REAGENT_SLOTS];
                        for (int i = 0; i < REAGENT_SLOTS; i++) {
                            req[i] = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        }
                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        boolean hasStrainId = buf.readBoolean();
                        Optional<String> strainId = hasStrainId ? Optional.of(buf.readUtf()) : Optional.empty();
                        return new SequencerRecipe(dna, base, req, result, strainId);
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, SequencerRecipe value) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, value.dnaIngredient);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, value.baseExtractIngredient);
                        for (Ingredient ing : value.requiredReagents) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                        }
                        ItemStack.STREAM_CODEC.encode(buf, value.result);
                        boolean hasId = value.strainId.isPresent();
                        buf.writeBoolean(hasId);
                        if (hasId) buf.writeUtf(value.strainId.get());
                    }
                };

        @Override
        public MapCodec<SequencerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SequencerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}