package net.micaxs.smokeleaf.item.custom;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BaseLampItem extends Item {
    private static final int TICKS_PER_SECOND = 20;


    public BaseLampItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int remainingTicks = stack.getMaxDamage() - stack.getDamageValue();
        if (remainingTicks < 0) remainingTicks = 0;

        long totalSeconds = (remainingTicks + (TICKS_PER_SECOND - 1)) / TICKS_PER_SECOND; // ceil
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        tooltipComponents.add(Component.literal(String.format("Lifespan: %02d:%02d", minutes, seconds)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.hps_lamp").withStyle(ChatFormatting.DARK_GRAY));

    }
}
