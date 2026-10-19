// file: 'src/main/java/net/micaxs/smokeleaf/recipe/BluntRecipe.java'
package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.item.custom.BluntItem;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BluntRecipe extends CustomRecipe {

    private final CraftingBookCategory category;
    private final Item bluntResult;

    public BluntRecipe(CraftingBookCategory category, Item bluntResult) {
        super();
        this.category = category;
        this.bluntResult = bluntResult;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return hasPattern(input);
    }

    private boolean hasPattern(CraftingInput input) {
        if (input.width() < 3 || input.height() < 3) return false;

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
        for (int i = 0; i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) nonEmpty++;
        }
        return nonEmpty == 9;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
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

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(bluntResult);
    }

    @Override
    public RecipeSerializer<BluntRecipe> getSerializer() {
        return ModRecipes.BLUNT_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer {

        public static final MapCodec<BluntRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("result")
                                .forGetter(r -> BuiltInRegistries.ITEM.getKey(r.bluntResult)),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(BluntRecipe::category)
                ).apply(instance, (resultRL, cat) ->
                        new BluntRecipe(
                                cat,
                                BuiltInRegistries.ITEM.getValue(resultRL)
                        ))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BluntRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(recipe.bluntResult));
                            buf.writeEnum(recipe.category());
                        },
                        buf -> {
                            Identifier res = buf.readIdentifier();
                            CraftingBookCategory cat = buf.readEnum(CraftingBookCategory.class);
                            return new BluntRecipe(
                                    cat,
                                    BuiltInRegistries.ITEM.getValue(res)
                            );
                        }
                );

        public MapCodec<BluntRecipe> codec() { return CODEC; }

        public StreamCodec<RegistryFriendlyByteBuf, BluntRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
