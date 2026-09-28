package net.micaxs.smokeleaf.item.custom;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import com.google.gson.JsonArray;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.utils.WeedEffectHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HashOilTinctureItem extends Item {
    public HashOilTinctureItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);


        JsonArray activeIngredients = ModDataComponentTypes.ACTIVE_INGREDIENTS.get(stack);
        if (activeIngredients == null || activeIngredients.isEmpty()) return;

        var weedItems = WeedEffectHelper.jsonArrayToWeedList(activeIngredients);
        if (!weedItems.isEmpty()) {
            tooltipComponents.add(Component.empty().append(
                    WeedEffectHelper.getEffectTooltip(weedItems, true)
            ));
        }
    }

}
