package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.GrowPotBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.screen.custom.MagnifyingGlassScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

        if (level.isClientSide) {
            openAnalyzerScreen(pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    public static void openAnalyzerScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new MagnifyingGlassScreen(pos));
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.plant_analyzer").withStyle(ChatFormatting.GRAY));
    }

}
