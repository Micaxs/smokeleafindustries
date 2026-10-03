package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
 * Like vanilla shapeless crafting, but the first ingredient matching {@code baseIngredient} is
 * transmuted into the result item rather than being consumed for a from-scratch result — so
 * enchantments, trims, custom names and durability all carry over, the same way the vanilla
 * netherite smithing upgrade preserves them. Used for Baja Hoodie -&gt; Reinforced Baja Hoodie.
 */
public class ArmorUpgradeShapelessRecipe implements CraftingRecipe {

    private final String group;
    private final CraftingBookCategory category;
    private final Ingredient baseIngredient;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final ShapelessRecipe delegate;

    public ArmorUpgradeShapelessRecipe(String group, CraftingBookCategory category, Ingredient baseIngredient,
                                        ItemStack result, NonNullList<Ingredient> ingredients) {
        this.group = group;
        this.category = category;
        this.baseIngredient = baseIngredient;
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
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (baseIngredient.test(stack)) {
                ItemStack upgraded = stack.transmuteCopy(result.getItem(), result.getCount());
                upgraded.applyComponents(result.getComponentsPatch());
                return upgraded;
            }
        }
        return ItemStack.EMPTY;
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
        return ModRecipes.ARMOR_UPGRADE_SHAPELESS_SERIALIZER.get();
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

    public static class Serializer implements RecipeSerializer<ArmorUpgradeShapelessRecipe> {
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

        public static final MapCodec<ArmorUpgradeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.EQUIPMENT).forGetter(recipe -> recipe.category),
                Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(recipe -> recipe.baseIngredient),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
        ).apply(instance, ArmorUpgradeShapelessRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ArmorUpgradeShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseIngredient);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                    }
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    Ingredient baseIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < size; i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    return new ArmorUpgradeShapelessRecipe(group, category, baseIngredient, result, ingredients);
                }
        );

        @Override
        public MapCodec<ArmorUpgradeShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArmorUpgradeShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
