package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class StrainCopyShapedRecipe implements CraftingRecipe {

    private final String group;
    private final CraftingBookCategory category;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;
    private final boolean showNotification;
    private final ShapedRecipe delegate;

    public StrainCopyShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.result = result;
        this.showNotification = showNotification;
        this.delegate = new ShapedRecipe(group, category, pattern, result, showNotification);
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
        return delegate.getIngredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRAIN_COPY_SHAPED_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public boolean showNotification() {
        return showNotification;
    }

    public String group() {
        return group;
    }

    public CraftingBookCategory category() {
        return category;
    }

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStack result() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<StrainCopyShapedRecipe> {

        public static final MapCodec<StrainCopyShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(StrainCopyShapedRecipe::group),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(StrainCopyShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(StrainCopyShapedRecipe::pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(StrainCopyShapedRecipe::result),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(StrainCopyShapedRecipe::showNotification)
        ).apply(instance, StrainCopyShapedRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapedRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    buf.writeBoolean(recipe.showNotification);
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    boolean showNotification = buf.readBoolean();
                    return new StrainCopyShapedRecipe(group, category, pattern, result, showNotification);
                }
        );

        @Override
        public MapCodec<StrainCopyShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StrainCopyShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
