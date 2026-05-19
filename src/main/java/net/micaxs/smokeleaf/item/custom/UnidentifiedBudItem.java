package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Bud item for custom strains.
 */
public class UnidentifiedBudItem extends BaseBudItem {

    public UnidentifiedBudItem(Properties properties, int dry, int dryingTime) {
        super(properties, dry, dryingTime);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d.identified() && d.displayName() != null && !d.displayName().isBlank()) {
            Component budName = Component.literal(d.displayName() + " Bud");
            boolean dried = Boolean.TRUE.equals(stack.get(ModDataComponentTypes.DRY));
            if (dried) {
                return Component.translatable("tooltip.smokeleafindustries.dried").append(" ").append(budName);
            } else {
                return Component.translatable("tooltip.smokeleafindustries.fresh").append(" ").append(budName);
            }
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

    public static int getThc(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        return d != StrainData.EMPTY ? d.thc() : BaseBudItem.getThc(stack);
    }

    public static int getCbd(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        return d != StrainData.EMPTY ? d.cbd() : BaseBudItem.getCbd(stack);
    }
}
