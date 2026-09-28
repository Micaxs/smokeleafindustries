package net.micaxs.smokeleaf.item.custom;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.micaxs.smokeleaf.fluid.StrainFluidContainerUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import java.util.List;

/**
 * Bucket item for unidentified mixture fluid.
 * Shows strain name and stats when the strain has been identified/named.
 */
public class UnidentifiedMixtureBucketItem extends BucketItem {

    public UnidentifiedMixtureBucketItem(java.util.function.Supplier<? extends Fluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    /**
     * Forge 1.20.1 only attaches a fluid handler to the exact {@link BucketItem} class, not subclasses,
     * so provide one here (1.21 registered it via RegisterCapabilitiesEvent). Without it the Mutator,
     * FluidUtil and pipes see this bucket as empty. The exposed FluidStack carries the bucket's strain.
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack) {
            @Override
            public FluidStack getFluid() {
                FluidStack fluid = super.getFluid();
                if (!fluid.isEmpty()) StrainFluidContainerUtil.copyStrainComponents(container, fluid);
                return fluid;
            }
        };
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
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = ModDataComponentTypes.STRAIN_DATA.get(stack);
        if (d == null) return;

        tooltip.add(Component.literal("Levels: ")
                .append(Component.literal(d.thc() + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" THC").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" & ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(d.cbd() + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" CBD").withStyle(ChatFormatting.DARK_GRAY)));

        if (!d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()).withStyle(ChatFormatting.GRAY));
        }
        StrainUtil.appendCreatorTooltip(stack, tooltip);
    }
}
