package net.micaxs.smokeleaf.item.custom;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BaseSeedItem extends BlockItem {
    private final String tooltipKey;

    public BaseSeedItem(net.minecraft.world.level.block.Block block, Properties properties, String tooltipKey) {
        super(block, properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipComponents, flag);
        tooltipComponents.accept(Component.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
    }
}
