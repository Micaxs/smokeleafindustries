package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class GenericGummyItem extends WeedDerivedItem {

    public GenericGummyItem(Properties properties) {
        super(properties, 1.5f, 1f, UseAnim.EAT, 40, 2);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Gummy Bear");
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return StrainUtil.defaultTintedInstance(this);
    }
}
