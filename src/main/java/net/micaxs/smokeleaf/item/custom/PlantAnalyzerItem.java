package net.micaxs.smokeleaf.item.custom;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.GrowPotBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.minecraft.ChatFormatting;
import net.micaxs.smokeleaf.client.ClientScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlantAnalyzerItem extends Item {

    public PlantAnalyzerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BaseWeedCropBlock)
                && !(state.getBlock() instanceof UnidentifiedWeedCropBlock)
                && !(state.getBlock() instanceof GrowPotBlock)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            ClientScreens.openAnalyzerScreen(pos);
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.smokeleafindustries.plant_analyzer").withStyle(ChatFormatting.GRAY));
    }

}
