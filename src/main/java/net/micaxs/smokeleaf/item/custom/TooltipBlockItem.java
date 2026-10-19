package net.micaxs.smokeleaf.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

/** Block item with fixed instruction lines (blocks can no longer add item tooltips themselves). */
public class TooltipBlockItem extends BlockItem {
    private final List<Component> lines;

    public TooltipBlockItem(Block block, Properties properties, List<Component> lines) {
        super(block, properties);
        this.lines = lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        lines.forEach(tooltip);
    }

    /** Grow Pot usage instructions (formerly GrowPotBlock#appendHoverText). */
    public static List<Component> growPotLines() {
        return List.of(
                Component.literal("Instructions:").withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.smokeleafindustries.grow_pot.soil").withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("tooltip.smokeleafindustries.grow_pot.plant").withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("tooltip.smokeleafindustries.grow_pot.info").withStyle(ChatFormatting.DARK_GRAY));
    }
}
