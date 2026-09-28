package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.DNAContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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

public record SequencerRecipe(ResourceLocation id, Ingredient dnaIngredient,
                              Ingredient baseExtractIngredient,
                              Ingredient[] requiredReagents,
                              ItemStack result,
                              Optional<String> strainId) implements Recipe<SequencerRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
    }


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
            boolean hasComp = ModDataComponentTypes.DNA_CONTENTS.has(input.dna());
        }

        if (!level.isClientSide()) {
            boolean hasComp = ModDataComponentTypes.DNA_CONTENTS.has(input.dna());
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
    public ItemStack assemble(SequencerRecipeInput input, RegistryAccess provider) {
        ItemStack out = result.copy();
        strainId.ifPresent(id -> {
            net.micaxs.smokeleaf.strain.StrainRegistry.get(id).ifPresent(strainData -> {
                ModDataComponentTypes.STRAIN_DATA.set(out, strainData);
                ModDataComponentTypes.STRAIN_ID.set(out, id);
            });
        });
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        ItemStack out = result.copy();
        strainId.ifPresent(id -> {
            net.micaxs.smokeleaf.strain.StrainRegistry.get(id).ifPresent(strainData -> {
                ModDataComponentTypes.STRAIN_DATA.set(out, strainData);
                ModDataComponentTypes.STRAIN_ID.set(out, id);
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

    public static class Serializer implements CodecRecipeSerializer<SequencerRecipe> {

        @Override
        public MapCodec<SequencerRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            CodecCompat.INGREDIENT.fieldOf("dna").forGetter(SequencerRecipe::dnaIngredient),
                            CodecCompat.INGREDIENT.fieldOf("base_extract").forGetter(SequencerRecipe::baseExtractIngredient),
                            CodecCompat.INGREDIENT_ALLOW_EMPTY.listOf().fieldOf("required_reagents")
                                    .flatXmap(list -> list.size() == REAGENT_SLOTS
                                                    ? DataResult.success(list)
                                                    : DataResult.<List<Ingredient>>error(() -> "required_reagents must have exactly " + REAGENT_SLOTS),
                                            DataResult::success)
                                    .forGetter(r -> java.util.List.of(r.requiredReagents)),
                            CodecCompat.ITEM_STACK.fieldOf("result").forGetter(SequencerRecipe::result),
                            Codec.STRING.optionalFieldOf("strain_id").forGetter(SequencerRecipe::strainId)
                    ).apply(instance, (dna, base, reagentsList, result, strainId) ->
                            new SequencerRecipe(id, dna, base, reagentsList.toArray(Ingredient[]::new), result, strainId))
            );
        }

        @Override
        public SequencerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient dna = Ingredient.fromNetwork(buf);
            Ingredient base = Ingredient.fromNetwork(buf);
            Ingredient[] req = new Ingredient[REAGENT_SLOTS];
            for (int i = 0; i < REAGENT_SLOTS; i++) {
                req[i] = Ingredient.fromNetwork(buf);
            }
            ItemStack result = buf.readItem();
            boolean hasStrainId = buf.readBoolean();
            Optional<String> strainId = hasStrainId ? Optional.of(buf.readUtf()) : Optional.empty();
            return new SequencerRecipe(id, dna, base, req, result, strainId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SequencerRecipe value) {
            value.dnaIngredient.toNetwork(buf);
            value.baseExtractIngredient.toNetwork(buf);
            for (Ingredient ing : value.requiredReagents) {
                ing.toNetwork(buf);
            }
            buf.writeItem(value.result);
            boolean hasId = value.strainId.isPresent();
            buf.writeBoolean(hasId);
            if (hasId) buf.writeUtf(value.strainId.get());
        }
    }
}
