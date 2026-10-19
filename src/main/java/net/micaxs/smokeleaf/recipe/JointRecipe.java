package net.micaxs.smokeleaf.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.custom.JointItem;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class JointRecipe extends CustomRecipe {

    private final CraftingBookCategory category;

    private static final TagKey<Item> JOINT_WEEDS = ModTags.WEEDS;
    private final Item tobaccoItem;
    private final Item jointResult;

    public JointRecipe(CraftingBookCategory category, Item tobaccoItem, Item jointResult) {
        super();
        this.category = category;
        this.tobaccoItem = tobaccoItem;
        this.jointResult = jointResult;
    }

    public Item getTobaccoItem() {
        return tobaccoItem;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return hasPattern(input);
    }

    private boolean hasPattern(CraftingInput input) {
        if (input.width() < 3 || input.height() < 3) return false;

        ItemStack pTop = input.getItem(1);
        ItemStack left = input.getItem(3);
        ItemStack mid = input.getItem(4);
        ItemStack right = input.getItem(5);
        ItemStack pBottom = input.getItem(7);

        if (pTop.isEmpty() || pBottom.isEmpty() || left.isEmpty() || right.isEmpty() || mid.isEmpty())
            return false;

        if (!pTop.is(net.minecraft.world.item.Items.PAPER) || !pBottom.is(net.minecraft.world.item.Items.PAPER))
            return false;

        // Only allow items in the weed tag (excludes extracts and others)
        if (!left.is(JOINT_WEEDS) || !right.is(JOINT_WEEDS))
            return false;

        if (mid.getItem() != tobaccoItem) return false;

        int nonEmpty = 0;
        for (int i = 0; i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) nonEmpty++;
        }
        return nonEmpty == 5;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        if (!hasPattern(input)) return ItemStack.EMPTY;
        ItemStack left = input.getItem(3);
        ItemStack right = input.getItem(5);
        List<ItemStack> weeds = new ArrayList<>(2);
        weeds.add(left);
        weeds.add(right);
        ItemStack joint = new ItemStack(jointResult);
        JointItem.storeWeeds(joint, weeds);
        return joint;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(jointResult);
    }

    @Override
    public RecipeSerializer<JointRecipe> getSerializer() {
        return ModRecipes.JOINT_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer {

        public static final MapCodec<JointRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("tobacco").forGetter(r -> BuiltInRegistries.ITEM.getKey(r.tobaccoItem)),
                        Identifier.CODEC.fieldOf("result").forGetter(r -> BuiltInRegistries.ITEM.getKey(r.jointResult)),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(JointRecipe::category)
                ).apply(instance, (tobaccoRL, resultRL, cat) ->
                        new JointRecipe(
                                cat,
                                BuiltInRegistries.ITEM.getValue(tobaccoRL),
                                BuiltInRegistries.ITEM.getValue(resultRL)
                        ))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, JointRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(recipe.tobaccoItem));
                            buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(recipe.jointResult));
                            buf.writeEnum(recipe.category());
                        },
                        buf -> {
                            Identifier tob = buf.readIdentifier();
                            Identifier res = buf.readIdentifier();
                            CraftingBookCategory cat = buf.readEnum(CraftingBookCategory.class);
                            return new JointRecipe(
                                    cat,
                                    BuiltInRegistries.ITEM.getValue(tob),
                                    BuiltInRegistries.ITEM.getValue(res)
                            );
                        }
                );

        public MapCodec<JointRecipe> codec() { return CODEC; }

        public StreamCodec<RegistryFriendlyByteBuf, JointRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
