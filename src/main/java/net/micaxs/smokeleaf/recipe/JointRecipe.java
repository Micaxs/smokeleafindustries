package net.micaxs.smokeleaf.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.custom.JointItem;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class JointRecipe extends CustomRecipe {

    private static final TagKey<Item> JOINT_WEEDS = ModTags.WEEDS;
    private final Item tobaccoItem;
    private final Item jointResult;

    public JointRecipe(ResourceLocation id, CraftingBookCategory category, Item tobaccoItem, Item jointResult) {
        super(id, category);
        this.tobaccoItem = tobaccoItem;
        this.jointResult = jointResult;
    }

    public Item getTobaccoItem() {
        return tobaccoItem;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return hasPattern(input);
    }

    private boolean hasPattern(CraftingContainer input) {
        if (input.getWidth() < 3 || input.getHeight() < 3) return false;

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
        for (int i = 0; i < input.getContainerSize(); i++) {
            if (!input.getItem(i).isEmpty()) nonEmpty++;
        }
        return nonEmpty == 5;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess provider) {
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

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= 3 && h >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return new ItemStack(jointResult);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.JOINT_SERIALIZER.get();
    }

    public static class Serializer implements CodecRecipeSerializer<JointRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MapCodec<JointRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ResourceLocation.CODEC.fieldOf("tobacco").forGetter(r -> BuiltInRegistries.ITEM.getKey(r.tobaccoItem)),
                            ResourceLocation.CODEC.fieldOf("result").forGetter(r -> BuiltInRegistries.ITEM.getKey(r.jointResult)),
                            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                    .forGetter(JointRecipe::category)
                    ).apply(instance, (tobaccoRL, resultRL, cat) ->
                            new JointRecipe(
                                    id,
                                    cat,
                                    BuiltInRegistries.ITEM.get(tobaccoRL),
                                    BuiltInRegistries.ITEM.get(resultRL)
                            ))
            );
        }

        @Override
        public JointRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ResourceLocation tob = buf.readResourceLocation();
            ResourceLocation res = buf.readResourceLocation();
            CraftingBookCategory cat = buf.readEnum(CraftingBookCategory.class);
            return new JointRecipe(
                    id,
                    cat,
                    BuiltInRegistries.ITEM.get(tob),
                    BuiltInRegistries.ITEM.get(res)
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, JointRecipe recipe) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.tobaccoItem));
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.jointResult));
            buf.writeEnum(recipe.category());
        }
    }
}
