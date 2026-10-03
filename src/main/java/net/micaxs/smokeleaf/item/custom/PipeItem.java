package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.micaxs.smokeleaf.block.entity.pipe.PipeConnection;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One of the three placeable pipes (item/fluid/energy). Right-clicking an existing pipe block
 * that doesn't yet have this type merges it in (up to the natural 3-type cap) instead of failing to
 * place — this is how up to 3 pipes end up sharing one block position. Falls back to normal placement
 * everywhere else, in which case {@link #placeBlock} marks the freshly created
 * {@link PipeBlockEntity} as carrying this type (a brand new {@code PipeBlockEntity} otherwise starts
 * with every type absent, which would place an invisible, collision-less block).
 */
public class PipeItem extends BlockItem {
    private final PipeType type;

    public PipeItem(Block block, PipeType type, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    public PipeType getPipeType() {
        return type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipe && !pipe.hasType(type)) {
            if (level instanceof ServerLevel serverLevel) {
                pipe.addType(serverLevel, type);
                level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                Player player = context.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useOn(context);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (!super.placeBlock(context, state)) return false;

        if (context.getLevel() instanceof ServerLevel serverLevel
                && serverLevel.getBlockEntity(context.getClickedPos()) instanceof PipeBlockEntity pipe
                && !pipe.hasType(type)) {
            pipe.addType(serverLevel, type);

            // Auto-wire toward whatever the pipe was placed against, mirroring Modern
            // Industrialization's "right-click again to connect" ergonomics but folded straight
            // into the initial placement so a pipe planted directly on a machine's face doesn't
            // need a separate wrench click just to hook it up.
            Direction backToNeighbor = context.getClickedFace().getOpposite();
            BlockPos neighborPos = context.getClickedPos().relative(backToNeighbor);
            if (!(serverLevel.getBlockEntity(neighborPos) instanceof PipeBlockEntity)
                    && PipeBlockEntity.neighborHasCapability(type, serverLevel, neighborPos, backToNeighbor.getOpposite())) {
                pipe.setConnection(serverLevel, type, backToNeighbor, PipeConnection.IMPORT);
            }
        }
        return true;
    }
}
