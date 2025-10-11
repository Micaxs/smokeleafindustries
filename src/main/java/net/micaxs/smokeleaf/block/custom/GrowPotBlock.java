package net.micaxs.smokeleaf.block.custom;

import com.mojang.serialization.MapCodec;
import net.micaxs.smokeleaf.block.entity.GrowPotBlockEntity;
import net.micaxs.smokeleaf.block.entity.ModBlockEntities;
import net.micaxs.smokeleaf.item.custom.FertilizerItem;
import net.micaxs.smokeleaf.item.custom.MagnifyingGlassItem;
import net.micaxs.smokeleaf.screen.custom.MagnifyingGlassScreen;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrowPotBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<GrowPotBlock> CODEC = simpleCodec(GrowPotBlock::new);

    public GrowPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(1.0D, 0.0D, 1.0D, 14.0D, 8.0D, 14.0D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GrowPotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.GROW_POT.get(), GrowPotBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof GrowPotBlockEntity pot)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean holdingBoneMeal = !stack.isEmpty() && stack.is(Items.BONE_MEAL);
        boolean holdingMagnifyingGlass = !stack.isEmpty() && stack.getItem() instanceof MagnifyingGlassItem;
        boolean holdingFertilizer = !stack.isEmpty() && stack.getItem() instanceof FertilizerItem;

        boolean canInsertSoil = !pot.hasSoil()
                && stack.getItem() instanceof BlockItem bi
                && bi.getBlock().builtInRegistryHolder().is(ModTags.POT_SOILS);

        boolean canPlantCrop = pot.hasSoil()
                && !pot.hasCrop()
                && stack.is(ModTags.WEED_SEEDS)
                && GrowPotBlockEntity.resolveCropBySeed(stack.getItem()) != null;

        boolean canFertilize = pot.hasCrop() && holdingFertilizer;
        boolean canBonemeal = pot.hasCrop() && holdingBoneMeal;
        boolean canHarvest = pot.canHarvest();
        boolean emptyHand = stack.isEmpty();
        boolean sneaking = player.isShiftKeyDown();

        if (holdingMagnifyingGlass) {
            if (level.isClientSide()) {
                Minecraft.getInstance().setScreen(new MagnifyingGlassScreen(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (level.isClientSide) {
            if ((sneaking && emptyHand && (pot.hasCrop() || pot.hasSoil()))
                    || canInsertSoil || canPlantCrop || canFertilize || canBonemeal || canHarvest) {
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (sneaking && emptyHand && level instanceof ServerLevel serverLevel) {
            if (pot.hasCrop()) {
                if (pot.removeCropAndGiveSeed(serverLevel, player)) {
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (pot.hasSoil()) {
                if (pot.removeSoilAndGiveBack(serverLevel, player)) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        if (canInsertSoil) {
            Block soilBlock = ((BlockItem) stack.getItem()).getBlock();
            pot.setSoil(soilBlock.defaultBlockState());
            if (!player.isCreative()) stack.shrink(1);
            pot.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        if (canPlantCrop) {
            BaseWeedCropBlock crop = GrowPotBlockEntity.resolveCropBySeed(stack.getItem());
            if (crop != null) {
                pot.initFromCrop(crop);
                pot.plantCrop(crop);
                if (!player.isCreative()) stack.shrink(1);
                pot.setChangedAndSync();
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (canFertilize && stack.getItem() instanceof FertilizerItem fert) {
            pot.addNitrogen(fert.getN());
            pot.addPhosphorus(fert.getP());
            pot.addPotassium(fert.getK());
            if (!player.isCreative()) stack.shrink(1);
            pot.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        if (canBonemeal && pot.applyBonemeal(level)) {
            if (!player.isCreative()) stack.shrink(1);
            level.levelEvent(1505, pos, 0);
            pot.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        if (canHarvest && level instanceof ServerLevel serverLevel) {
            pot.harvest(serverLevel);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal("Instructions:").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.grow_pot.soil").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.grow_pot.plant").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.grow_pot.info").withStyle(ChatFormatting.DARK_GRAY));
    }
}
