package net.micaxs.smokeleaf.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Plain {@link ArmorItem} that also advertises the Baja Hoodie set's "negates Stoned" bonus in
 * its tooltip (the bonus itself is applied mod-wide in CommonEvents, based on item identity, not
 * anything this class does).
 */
public class BajaHoodieArmorItem extends ArmorItem {
    public BajaHoodieArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.smokeleafindustries.baja_hoodie.set_bonus").withStyle(ChatFormatting.DARK_GREEN));
    }
}
