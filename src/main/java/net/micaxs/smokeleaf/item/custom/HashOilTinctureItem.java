package net.micaxs.smokeleaf.item.custom;

import com.google.gson.JsonArray;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
//import net.micaxs.smokeleaf.utils.HashOilHelper;
import net.micaxs.smokeleaf.utils.WeedEffectHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class HashOilTinctureItem extends Item {
    public HashOilTinctureItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);


        JsonArray activeIngredients = stack.get(ModDataComponentTypes.ACTIVE_INGREDIENTS);
        if (activeIngredients == null || activeIngredients.isEmpty()) return;

        var weedItems = WeedEffectHelper.jsonArrayToWeedList(activeIngredients);
        if (!weedItems.isEmpty()) {
            tooltipComponents.add(Component.empty().append(
                    WeedEffectHelper.getEffectTooltip(weedItems, true)
            ));
        }
    }

}
