package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
}
