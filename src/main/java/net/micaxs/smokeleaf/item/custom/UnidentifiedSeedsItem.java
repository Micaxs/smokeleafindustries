package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

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
    public InteractionResult useOn(UseOnContext context) {
        StrainData d = StrainUtil.getStrain(context.getItemInHand());
        if (!d.identified()) {
            if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("tooltip.smokeleafindustries.unidentified_seed_cannot_plant"), true);
            }
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return StrainUtil.defaultTintedInstance(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d == null) return;
        if (!d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()));
        }
        StrainUtil.appendCreatorTooltip(stack, tooltip);
    }
}
