package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

/**
 * Like vanilla shapeless crafting but copies STRAIN_DATA from the first
 * ingredient that carries it to the result. Used for bag ↔ weed recipes.
 */
public class StrainCopyShapelessRecipe implements CraftingRecipe {

    private final String group;
    private final CraftingBookCategory category;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final ShapelessRecipe delegate;

    public StrainCopyShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = ingredients;
        this.delegate = new ShapelessRecipe(group, category, result, ingredients);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return delegate.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack crafted = result.copy();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            StrainData sd = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
            if (sd != null) {
                crafted.set(ModDataComponentTypes.STRAIN_DATA.get(), sd);
                break;
            }
        }
        return crafted;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return delegate.canCraftInDimensions(w, h);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRAIN_COPY_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public String group() {
        return group;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer implements RecipeSerializer<StrainCopyShapelessRecipe> {
        private static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf().flatXmap(
                ingredients -> {
                    Ingredient[] values = ingredients.toArray(Ingredient[]::new);
                    if (values.length == 0) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    }
                    if (values.length > 9) {
                        return DataResult.error(() -> "Too many ingredients for shapeless recipe");
                    }
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, values));
                },
                DataResult::success
        );

        public static final MapCodec<StrainCopyShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
        ).apply(instance, StrainCopyShapelessRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                    }
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < size; i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    return new StrainCopyShapelessRecipe(group, category, result, ingredients);
                }
        );

        @Override
        public MapCodec<StrainCopyShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
