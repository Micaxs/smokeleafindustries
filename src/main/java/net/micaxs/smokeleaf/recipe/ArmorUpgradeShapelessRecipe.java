package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Like vanilla shapeless crafting, but the first ingredient matching {@code baseIngredient} is
 * transmuted into the result item rather than being consumed for a from-scratch result — so
 * enchantments, trims, custom names and durability all carry over, the same way the vanilla
 * netherite smithing upgrade preserves them. Used for Baja Hoodie -&gt; Reinforced Baja Hoodie.
 */
public class ArmorUpgradeShapelessRecipe extends ShapelessRecipe {

    private final Ingredient baseIngredient;
    private final ItemStack result;

    public ArmorUpgradeShapelessRecipe(ResourceLocation id, String group, CraftingBookCategory category, Ingredient baseIngredient,
                                       ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, category, result, ingredients);
        this.baseIngredient = baseIngredient;
        this.result = result;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (baseIngredient.test(stack)) {
                // Transmute: new item, same NBT (enchantments, name, damage, trims), then any result NBT on top.
                ItemStack upgraded = new ItemStack(result.getItem(), result.getCount());
                CompoundTag tag = stack.getTag() != null ? stack.getTag().copy() : new CompoundTag();
                if (result.getTag() != null) {
                    CompoundTag resultTag = result.getTag().copy();
                    resultTag.remove("Damage"); // keep the base piece's durability
                    tag.merge(resultTag);
                }
                if (!tag.isEmpty()) upgraded.setTag(tag);
                return upgraded;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ARMOR_UPGRADE_SHAPELESS_SERIALIZER.get();
    }

    public Ingredient baseIngredient() {
        return baseIngredient;
    }

    public ItemStack result() {
        return result;
    }

    public static class Serializer implements CodecRecipeSerializer<ArmorUpgradeShapelessRecipe> {

        @Override
        public MapCodec<ArmorUpgradeShapelessRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.EQUIPMENT).forGetter(ShapelessRecipe::category),
                    CodecCompat.INGREDIENT.fieldOf("base").forGetter(ArmorUpgradeShapelessRecipe::baseIngredient),
                    CodecCompat.ITEM_STACK.fieldOf("result").forGetter(ArmorUpgradeShapelessRecipe::result),
                    StrainCopyShapelessRecipe.INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(ShapelessRecipe::getIngredients)
            ).apply(instance, (group, category, base, result, ingredients) ->
                    new ArmorUpgradeShapelessRecipe(id, group, category, base, result, ingredients)));
        }

        @Override
        public ArmorUpgradeShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient baseIngredient = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < size; i++) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }
            return new ArmorUpgradeShapelessRecipe(id, group, category, baseIngredient, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ArmorUpgradeShapelessRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            recipe.baseIngredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
        }
    }
}
