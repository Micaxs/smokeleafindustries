package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.world.item.ItemStack;

/**
 * Bud item for custom strains.
 * Naming and hover text are handled by BaseBudItem (reads STRAIN_DATA).
 */
public class UnidentifiedBudItem extends BaseBudItem {

    public UnidentifiedBudItem(Properties properties, int dry, int dryingTime) {
        super(properties, dry, dryingTime);
    }

    public static int getThc(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        return d != StrainData.EMPTY ? d.thc() : BaseBudItem.getThc(stack);
    }

    public static int getCbd(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        return d != StrainData.EMPTY ? d.cbd() : BaseBudItem.getCbd(stack);
    }
}

