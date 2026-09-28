package net.micaxs.smokeleaf.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.custom.BaseWeedItem;
import net.micaxs.smokeleaf.item.custom.WeedDerivedItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeedEffectHelper {

    public static void setWeedData(CraftingContainer container, ItemStack weedDerivedItem) {
        int finalDuration = 0;
        for (ItemStack ingredient : container.getItems()) {
            if (!(ingredient.getItem() instanceof BaseWeedItem weedItem) || !(weedDerivedItem.getItem() instanceof WeedDerivedItem)) {
                continue;
            }
            // Duration Tag
            float effectMultiplier = ((WeedDerivedItem) weedDerivedItem.getItem()).getEffectFactor();
            finalDuration += (int) (weedItem.getDuration() * effectMultiplier);

            var weedDerivedItemDuration = ModDataComponentTypes.EFFECT_DURATION.get(weedDerivedItem);
            if (weedDerivedItemDuration != null) {
                ModDataComponentTypes.EFFECT_DURATION.set(weedDerivedItem, finalDuration);
            }
            ModDataComponentTypes.ACTIVE_INGREDIENT.set(weedDerivedItem, ingredient.getItem().getDescriptionId());

            nameWeedBasedItem(ingredient, weedDerivedItem);
        }
    }

    public static void nameWeedBasedItem(ItemStack weedStack, ItemStack weedBasedItem) {
        if (!(weedStack.getItem() instanceof BaseWeedItem)) {
            return;
        }

        Component weedNameComponent = weedStack.getItem().getName(weedStack);
        Component weedBasedItemName = weedBasedItem.getItem().getName(weedBasedItem);
        String weedName = weedNameComponent.getString().replace(" Weed", "").replace(" weed", "");

        weedBasedItem.setHoverName( Component.literal(weedName.concat(" ").concat(weedBasedItemName.getString())));

    }


    public static List<BaseWeedItem> jsonArrayToWeedList(JsonArray array) {
        List<BaseWeedItem> list = new ArrayList<>();
        if (array == null || array.isEmpty()) return list;

        for (JsonElement el : array) {
            String id = null;
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                id = el.getAsString();
            } else if (el.isJsonObject() && el.getAsJsonObject().has("id")) {
                id = el.getAsJsonObject().get("id").getAsString();
            }
            if (id == null) continue;

            ResourceLocation rl = ResourceLocation.tryParse(id);
            if (rl == null) continue;

            var item = BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
            if (item instanceof BaseWeedItem weedItem) {
                list.add(weedItem);
            }
        }
        return list;
    }


    public static Component getEffectTooltip(MobEffect effect, int effectDuration, boolean showDuration) {
        if (showDuration) {
            int duration = effectDuration / 20; // Convert duration to seconds
            return Component.translatable(effect.getDescriptionId())
                    .append(" ")
                    .append(Component.literal("(" + duration + "s)"))
                    .withStyle(ChatFormatting.GREEN);
        }

        return Component.translatable(effect.getDescriptionId())
                .append(" ")
                .append(Component.literal("(??)"))
                .withStyle(ChatFormatting.GREEN);
    }

    public static Component getEffectTooltip(List<BaseWeedItem> activeIngredients, boolean showDuration) {
        MutableComponent result = Component.empty();
        boolean first = true;
        for (BaseWeedItem weed : activeIngredients) {
            if (!first) {
                result.append("\n");
            }
            result.append(getEffectTooltip(weed.getEffect(), weed.getDuration(), showDuration));
            first = false;
        }
        return result;
    }

    public static @Nullable BaseWeedItem getActiveWeedIngredient(ItemStack itemStack) {
        String activeIngredient = ModDataComponentTypes.ACTIVE_INGREDIENT.get(itemStack);
        if (activeIngredient != null) {
            ResourceLocation id = ResourceLocation.tryParse(activeIngredient);
            if (id != null) {
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item instanceof BaseWeedItem weed) {
                    return weed;
                }
            }
        }
        return null;
    }


}
