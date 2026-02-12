// src/main/java/net/micaxs/smokeleaf/item/custom/FertilizerItem.java
package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.block.entity.BaseWeedCropBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FertilizerItem extends Item {
    private final int nitrogen;
    private final int phosphorus;
    private final int potassium;

    public FertilizerItem(int n, int p, int k, Properties properties) {
        super(properties);
        this.nitrogen = n;
        this.phosphorus = p;
        this.potassium = k;
    }

    public int getN() { return nitrogen; }
    public int getP() { return phosphorus; }
    public int getK() { return potassium; }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (!(clickedState.getBlock() instanceof BaseWeedCropBlock)
                && !(clickedState.getBlock() instanceof UnidentifiedWeedCropBlock)) {
            return InteractionResult.PASS;
        }

        // Always operate on the bottom block entity
        BlockPos bePos = clickedPos;
        if ((clickedState.getBlock() instanceof BaseWeedCropBlock && clickedState.getValue(BaseWeedCropBlock.TOP))
                || (clickedState.getBlock() instanceof UnidentifiedWeedCropBlock && clickedState.getValue(UnidentifiedWeedCropBlock.TOP))) {
            bePos = clickedPos.below();
        }

        // Check if the crop is fully grown (use bottom state for safety)
        BlockState bottomState = level.getBlockState(bePos);
        boolean isFullyGrown = false;
        if (bottomState.getBlock() instanceof BaseWeedCropBlock bottomCrop) {
            isFullyGrown = bottomCrop.isMaxAge(bottomState);
        } else if (bottomState.getBlock() instanceof UnidentifiedWeedCropBlock unidentifiedCrop) {
            isFullyGrown = bottomState.getValue(UnidentifiedWeedCropBlock.AGE) >= unidentifiedCrop.getMaxAge();
        }

        if (isFullyGrown) {
            if (!level.isClientSide() && context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("tooltip.smokeleafindustries.add_fertilizer"),
                        true
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (!level.isClientSide) {
            BaseWeedCropBlockEntity be = getCropBE(level, bePos);
            if (be != null) {
                be.addNitrogen(this.nitrogen);
                be.addPhosphorus(this.phosphorus);
                be.addPotassium(this.potassium);
                be.sync();

                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    ItemStack stack = context.getItemInHand();
                    stack.shrink(1);
                }
                return InteractionResult.CONSUME; // server consumed
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BaseWeedCropBlockEntity getCropBE(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof BaseWeedCropBlockEntity crop) ? crop : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        addStat(tooltip, "Nitrogen (N): ", getN(), ChatFormatting.DARK_GREEN);
        addStat(tooltip, "Phosphorus (P): ", getP(), ChatFormatting.DARK_AQUA);
        addStat(tooltip, "Potassium (K): ", getK(), ChatFormatting.GOLD);
    }

    private static void addStat(List<Component> tooltip, String label, int value, ChatFormatting labelColor) {
        tooltip.add(
                Component.literal(label)
                        .withStyle(labelColor)
                        .append(Component.literal(String.format("%+d", value)).withStyle(ChatFormatting.GRAY))
        );
    }
}
