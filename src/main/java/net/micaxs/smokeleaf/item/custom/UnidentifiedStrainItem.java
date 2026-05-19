package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Base class for "unidentified" custom strain items.
 *
 * - Stores all gameplay data inside the STRAIN_DATA data component.
 * - Uses custom hover name when identified.
 */
public class UnidentifiedStrainItem extends Item {

    public UnidentifiedStrainItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName());
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
