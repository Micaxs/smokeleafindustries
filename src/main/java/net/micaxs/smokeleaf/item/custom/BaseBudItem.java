package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class BaseBudItem extends Item {

    public int dry;
    public int dryingTime;

    public BaseBudItem(Properties properties, int iDry, int iDryingTime) {
        super(properties);
        this.dry = iDry;
        this.dryingTime = iDryingTime;
    }

    // Optional: constructor with default THC/CBD for new stacks
    public BaseBudItem(Properties properties, int iDry, int iDryingTime, int defaultThc, int defaultCbd) {
        super(withDefaults(properties, defaultThc, defaultCbd));
        this.dry = iDry;
        this.dryingTime = iDryingTime;
    }

    private static Properties withDefaults(Properties properties, int defaultThc, int defaultCbd) {
        return properties
                .component(ModDataComponentTypes.THC.get(), defaultThc)
                .component(ModDataComponentTypes.CBD.get(), defaultCbd);
    }

    public boolean isDry() {
        return dry != 0;
    }

    @Override
    public Component getName(ItemStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        Component baseName;
        if (d != StrainData.EMPTY && d.displayName() != null && !d.displayName().isBlank()) {
            baseName = Component.literal(d.displayName() + " Bud");
        } else {
            baseName = Component.translatable(stack.getDescriptionId());
        }
        boolean dried = Boolean.TRUE.equals(stack.get(ModDataComponentTypes.DRY));
        if (dried) {
            return Component.translatable("tooltip.smokeleafindustries.dried").append(" ").append(baseName);
        } else {
            return Component.translatable("tooltip.smokeleafindustries.fresh").append(" ").append(baseName);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StrainData d = stack.get(ModDataComponentTypes.STRAIN_DATA.get());
        if (d == null) return;
        tooltip.add(getLevelsText(d));
        if (!d.effects().isEmpty()) {
            tooltip.add(Component.literal("Effects: " + d.effects().size()));
        }
    }

    private Component getLevelsText(StrainData d) {
        return Component.literal("Levels: ")
                .append(Component.literal(d.thc() + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" THC").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" & ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(d.cbd() + "%").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" CBD").withStyle(ChatFormatting.DARK_GRAY));
    }


    // Helpers
    public static void changeDryStatus(ItemStack stack, boolean value) {
        stack.set(ModDataComponentTypes.DRY, value);
    }

    public static int getThc(ItemStack stack) {
        Integer v = stack.get(ModDataComponentTypes.THC);
        return v != null ? v : 0;
    }

    public static int getCbd(ItemStack stack) {
        Integer v = stack.get(ModDataComponentTypes.CBD);
        return v != null ? v : 0;
    }

    public static void setThc(ItemStack stack, int value) {
        stack.set(ModDataComponentTypes.THC, value);
    }

    public static void setCbd(ItemStack stack, int value) {
        stack.set(ModDataComponentTypes.CBD, value);
    }

    public static void addThc(ItemStack stack, int delta) {
        setThc(stack, getThc(stack) + delta);
    }

    public static void addCbd(ItemStack stack, int delta) {
        setCbd(stack, getCbd(stack) + delta);
    }


}
