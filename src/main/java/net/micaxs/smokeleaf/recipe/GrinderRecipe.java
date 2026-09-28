package net.micaxs.smokeleaf.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record GrinderRecipe(ResourceLocation id, Ingredient inputItem, ItemStack output) implements Recipe<GrinderRecipeInput> {

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // Machine recipe: keep it out of the vanilla recipe book (avoids "Unknown recipe category" spam).
    @Override
    public boolean isSpecial() {
        return true;
    }


    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(GrinderRecipeInput grinderRecipeInput, Level level) {
        return inputItem.test(grinderRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(GrinderRecipeInput grinderRecipeInput, RegistryAccess provider) {
        ItemStack out = output.copy();
        ItemStack in = grinderRecipeInput.getItem(0);
        if (!in.isEmpty()) {
            StrainData sd = ModDataComponentTypes.STRAIN_DATA.get(in);
            if (sd != null) {
                // STRAIN_DATA is the source of truth — skip legacy initializeStack to avoid
                // stamping individual thc/cbd/active_ingredient/effect_duration components.
                ModDataComponentTypes.STRAIN_DATA.set(out, sd);
            } else {
                // Legacy path: initialize weed defaults then copy individual components.
                if (out.getItem() instanceof BaseWeedItem weedItem) {
                    weedItem.initializeStack(out);
                }
                Integer thc = ModDataComponentTypes.THC.get(in);
                Integer cbd = ModDataComponentTypes.CBD.get(in);
                if (thc != null) ModDataComponentTypes.THC.set(out, thc);
                if (cbd != null) ModDataComponentTypes.CBD.set(out, cbd);
            }
        }
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GRINDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements CodecRecipeSerializer<GrinderRecipe> {
        @Override
        public MapCodec<GrinderRecipe> codec(ResourceLocation id) {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    CodecCompat.INGREDIENT.fieldOf("ingredient").forGetter(GrinderRecipe::inputItem),
                    CodecCompat.ITEM_STACK.fieldOf("result").forGetter(GrinderRecipe::output)
            ).apply(inst, (ing, out) -> new GrinderRecipe(id, ing, out)));
        }

        @Override
        public GrinderRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new GrinderRecipe(id, Ingredient.fromNetwork(buf), buf.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GrinderRecipe recipe) {
            recipe.inputItem().toNetwork(buf);
            buf.writeItem(recipe.output());
        }
    }
}
