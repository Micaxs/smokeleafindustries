package net.micaxs.smokeleaf.item.custom;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HempHammer extends Item {
    private static final RandomSource RANDOM = RandomSource.create();

    public HempHammer(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        ItemStack copy = switch (instance) {
            case ItemStack stack -> stack.copyWithCount(1);
            case ItemStackTemplate template -> template.create().copyWithCount(1);
            default -> new ItemStack(this);
        };
        if (shouldConsumeDurability(copy)) {
            copy.setDamageValue(copy.getDamageValue() + 1);
        }
        if (copy.getDamageValue() >= copy.getMaxDamage()) {
            return null;
        }
        return ItemStackTemplate.fromNonEmptyStack(copy);
    }

    private static boolean shouldConsumeDurability(ItemStack stack) {
        int unbreakingLevel = getUnbreakingLevel(stack);
        return unbreakingLevel <= 0 || RANDOM.nextInt(unbreakingLevel + 1) == 0;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING) || enchantment.value().isSupportedItem(stack);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) > 0) {
            stack.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int unbreakingLevel = getUnbreakingLevel(stack);
        int useMultiplier = unbreakingLevel + 1;
        int max = stack.getMaxDamage();
        int used = stack.getDamageValue();
        int remaining = (max - used) * useMultiplier;
        int total = max * useMultiplier;
        double ratio = total > 0 ? (double) remaining / total : 0.0;

        ChatFormatting color;
        if (ratio > 0.5) {
            color = ChatFormatting.GREEN;
        } else if (ratio > 0.25) {
            color = ChatFormatting.GOLD; // orange-ish
        } else {
            color = ChatFormatting.RED;
        }

        tooltipComponents.accept(
                Component.translatable("tooltip.smokeleafindustries.hemp_hammer.uses", remaining, total)
                        .withStyle(color)
        );

        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    private static int getUnbreakingLevel(ItemStack stack) {
        Holder<Enchantment> unbreaking = CommonHooks.resolveLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        return stack.getEnchantmentLevel(unbreaking);
    }
}
