package net.micaxs.smokeleaf.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Plain armor item (configured through {@link Item.Properties#humanoidArmor}) that also advertises
 * the Baja Hoodie set's "negates Stoned" bonus in its tooltip (the bonus itself is applied mod-wide
 * in CommonEvents, based on item identity, not anything this class does).
 */
public class BajaHoodieArmorItem extends Item {
    public BajaHoodieArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.smokeleafindustries.baja_hoodie.set_bonus").withStyle(ChatFormatting.DARK_GREEN));
    }
}
