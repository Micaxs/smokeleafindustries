package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * Seeds that carry custom strain data.
 *
 * For now, this plants the existing hemp crop block (GrowPot handles the dynamic strain data).
 * If you later add an in-world custom crop block, swap the planted block here.
 */
public class UnidentifiedSeedsItem extends ItemNameBlockItem {
    public UnidentifiedSeedsItem(Properties properties) {
        super(ModBlocks.UNIDENTIFIED_WEED_CROP.get(), properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Slight visual hint for identified strains
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        return d != null && d.identified();
    }
}
