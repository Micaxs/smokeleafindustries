package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Seeds that carry custom strain data.
 */
public class UnidentifiedSeedsItem extends ItemNameBlockItem {
    public UnidentifiedSeedsItem(Properties properties) {
        super(ModBlocks.UNIDENTIFIED_WEED_CROP.get(), properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Seeds");
        }
        return super.getName(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d == null) return;
        MutableComponent stats = Component.literal("THC: " + d.thc() + "%  CBD: " + d.cbd() + "%");
        tooltip.add(stats);
        if (!d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()));
        }
    }
}
