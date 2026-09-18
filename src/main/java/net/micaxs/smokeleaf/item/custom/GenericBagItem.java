package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GenericBagItem extends Item {
    public GenericBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Bag");
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return StrainUtil.defaultTintedInstance(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d != null && !d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()).withStyle(ChatFormatting.GRAY));
        }
        StrainUtil.appendCreatorTooltip(stack, tooltip);
    }
}
