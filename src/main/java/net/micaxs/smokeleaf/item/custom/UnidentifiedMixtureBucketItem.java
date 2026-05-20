package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

/**
 * Bucket item for unidentified mixture fluid.
 * Shows strain name and stats when the strain has been identified/named.
 */
public class UnidentifiedMixtureBucketItem extends BucketItem {

    public UnidentifiedMixtureBucketItem(Fluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Oil Bucket");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d == null) return;
        MutableComponent stats = Component.literal("THC: " + d.thc() + "%  CBD: " + d.cbd() + "%");
        tooltip.add(stats);
        tooltip.add(Component.literal("NPK: " + d.nitrogen() + "/" + d.phosphorus() + "/" + d.potassium()));
        if (!d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()));
        }
    }
}
