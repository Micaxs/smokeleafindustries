package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class GenericGummyWormItem extends WeedDerivedItem {

    public GenericGummyWormItem(Properties properties) {
        // Slightly shorter STONED duration than the gummy bear (1.5f) per its recipe's design.
        super(properties, 1.3f, 1f, UseAnim.EAT, 40, 2);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Gummy Worm");
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return StrainUtil.defaultTintedInstance(this);
    }
}
