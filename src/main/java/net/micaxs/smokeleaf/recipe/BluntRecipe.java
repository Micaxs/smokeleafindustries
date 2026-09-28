// file: 'src/main/java/net/micaxs/smokeleaf/recipe/BluntRecipe.java'
package net.micaxs.smokeleaf.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.item.custom.BluntItem;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BluntRecipe extends CustomRecipe {
    private final Item bluntResult;

    public BluntRecipe(ResourceLocation id, CraftingBookCategory category, Item bluntResult) {
        super(id, category);
        this.bluntResult = bluntResult;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return hasPattern(input);
    }

    private boolean hasPattern(CraftingContainer input) {
        if (input.getWidth() < 3 || input.getHeight() < 3) return false;

        // Indices:
        // 0 1 2
        // 3 4 5
        // 6 7 8
        ItemStack p0 = input.getItem(0);
        ItemStack p1 = input.getItem(1);
        ItemStack p2 = input.getItem(2);
        ItemStack w3 = input.getItem(3);
        ItemStack w4 = input.getItem(4);
        ItemStack w5 = input.getItem(5);
        ItemStack p6 = input.getItem(6);
        ItemStack p7 = input.getItem(7);
        ItemStack p8 = input.getItem(8);

        // Require all 9 slots present
        if (p0.isEmpty() || p1.isEmpty() || p2.isEmpty()
                || w3.isEmpty() || w4.isEmpty() || w5.isEmpty()
                || p6.isEmpty() || p7.isEmpty() || p8.isEmpty()) {
            return false;
        }

        // Top and bottom rows: paper
        if (!p0.is(Items.PAPER) || !p1.is(Items.PAPER) || !p2.is(Items.PAPER)) return false;
        if (!p6.is(Items.PAPER) || !p7.is(Items.PAPER) || !p8.is(Items.PAPER)) return false;

        // Middle row: only items in ModTags.WEEDS
        if (!w3.is(ModTags.WEEDS) || !w4.is(ModTags.WEEDS) || !w5.is(ModTags.WEEDS)) return false;

        // Ensure no extras outside the 3x3 we care about (for larger grids)
        int nonEmpty = 0;
        for (int i = 0; i < input.getContainerSize(); i++) {
            if (!input.getItem(i).isEmpty()) nonEmpty++;
        }
        return nonEmpty == 9;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess provider) {
        if (!hasPattern(input)) return ItemStack.EMPTY;

        ItemStack w3 = input.getItem(3);
        ItemStack w4 = input.getItem(4);
        ItemStack w5 = input.getItem(5);

        List<ItemStack> weeds = new ArrayList<>(3);
        weeds.add(w3);
        weeds.add(w4);
        weeds.add(w5);

        ItemStack blunt = new ItemStack(bluntResult);
        BluntItem.storeWeeds(blunt, weeds);
        return blunt;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= 3 && h >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return new ItemStack(bluntResult);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BLUNT_SERIALIZER.get();
    }

    public static class Serializer implements CodecRecipeSerializer<BluntRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<BluntRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ResourceLocation.CODEC.fieldOf("result")
                                    .forGetter(r -> BuiltInRegistries.ITEM.getKey(r.bluntResult)),
                            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                    .forGetter(BluntRecipe::category)
                    ).apply(instance, (resultRL, cat) ->
                            new BluntRecipe(
                                    id,
                                    cat,
                                    BuiltInRegistries.ITEM.get(resultRL)
                            ))
            );
        }

        @Override
        public BluntRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ResourceLocation res = buf.readResourceLocation();
            CraftingBookCategory cat = buf.readEnum(CraftingBookCategory.class);
            return new BluntRecipe(id, cat, BuiltInRegistries.ITEM.get(res));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BluntRecipe recipe) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.bluntResult));
            buf.writeEnum(recipe.category());
        }
    }
}
