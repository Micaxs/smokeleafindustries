package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.component.ManualGrinderContents;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.recipe.ManualGrinderInput;
import net.micaxs.smokeleaf.recipe.ManualGrinderRecipe;
import net.micaxs.smokeleaf.recipe.ModRecipes;
import net.micaxs.smokeleaf.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ManualGrinderItem extends Item {

    public ManualGrinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack grinder = player.getItemInHand(hand);
        ItemStack stored = getStored(grinder);
        if (stored.isEmpty()) return InteractionResultHolder.pass(grinder);
        if (!isValidIngredient(level, stored)) return InteractionResultHolder.fail(grinder);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(grinder);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if (!(entity instanceof Player player)) return;

        ItemStack stored = getStored(stack);
        if (stored.isEmpty()) {
            player.releaseUsingItem();
            return;
        }

        Optional<ManualGrinderRecipe> match = getRecipe(level, stored);
        if (match.isEmpty()) {
            player.releaseUsingItem();
            return;
        }

        int used = getUseDuration(stack, entity) - remainingUseTicks;
        int needed = match.get().grindTime();

        if (used % 10 == 0 && level.isClientSide) {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    ModSounds.MANUAL_GRINDER.get(), SoundSource.PLAYERS, 0.25F, 1.0F, false);
        }

        if (used >= needed) {
            if (!level.isClientSide) {
                ManualGrinderRecipe recipe = match.get();

                // Assemble with the real stored bud so THC/CBD are copied to the result
                ItemStack result = recipe.assemble(
                        new ManualGrinderInput(stored.copyWithCount(1)),
                        level.registryAccess()
                );

                // Propagate strain lineage from bud to weed
                var strainData = stored.get(ModDataComponentTypes.STRAIN_DATA.get());
                if (strainData != null) result.set(ModDataComponentTypes.STRAIN_DATA.get(), strainData);
                var strainId = stored.get(ModDataComponentTypes.STRAIN_ID.get());
                if (strainId != null) result.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);

                boolean bonus = isDriedBud(stored);

                // Consume one input and persist the remainder
                stored.shrink(1);
                if (stored.isEmpty()) {
                    stack.remove(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get());
                } else {
                    stack.set(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get(),
                            ManualGrinderContents.fromStack(stored));
                }

                if (bonus) {
                    result.grow(1);
                }

                if (!player.addItem(result)) player.drop(result, false);
                level.playSound(null, player.blockPosition(), ModSounds.MANUAL_GRINDER.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
            }
            player.releaseUsingItem();
        }
    }

    private boolean isDriedBud(ItemStack stack) {
        if (!(stack.getItem() instanceof BaseBudItem)) return false;
        Boolean dry = stack.get(ModDataComponentTypes.DRY);
        return Boolean.TRUE.equals(dry);
    }

    private Optional<ManualGrinderRecipe> getRecipe(Level level, ItemStack ingredient) {
        if (level == null || ingredient.isEmpty()) return Optional.empty();
        ManualGrinderInput input = new ManualGrinderInput(ingredient.copyWithCount(1));
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.MANUAL_GRINDER_TYPE.get(), input, level)
                .map(holder -> holder.value());
    }

    private boolean isValidIngredient(Level level, ItemStack ingredient) {
        return getRecipe(level, ingredient).isPresent();
    }

    private ItemStack getStored(ItemStack grinder) {
        ManualGrinderContents data = grinder.get(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get());
        return data == null ? ItemStack.EMPTY : data.toStack();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemStack stored = getStored(stack);
        if (stored.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.smokeleafindustries.manual_grinder.empty").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.smokeleafindustries.manual_grinder.usage_empty")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.smokeleafindustries.manual_grinder.contains",
                    stored.getHoverName()).withStyle(ChatFormatting.GREEN));
            boolean valid = context.level() != null && isValidIngredient(context.level(), stored);
            if (!valid) {
                tooltip.add(Component.translatable("tooltip.smokeleafindustries.manual_grinder.invalid")
                        .withStyle(ChatFormatting.RED));
            }
            tooltip.add(Component.translatable("tooltip.smokeleafindustries.manual_grinder.usage_full")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
