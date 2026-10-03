package net.micaxs.smokeleaf.event;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.custom.LogisticsPipeBlock;
import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.micaxs.smokeleaf.block.entity.pipe.PipeConnection;
import net.micaxs.smokeleaf.block.entity.pipe.PipeHitPart;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.micaxs.smokeleaf.item.custom.PipeWrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Drives all Pipe Wrench interaction. Ported from Modern Industrialization's wrench UX (MIT
 * licensed — {@code pipes/impl/PipeBlock#useWrench}, dispatched via a global
 * {@code PlayerInteractEvent.RightClickBlock} listener exactly like MI's own {@code MI.java}):
 * the exact part hit by the raycast — a type's lane connector, or one of its existing connector
 * arms — determines what happens, so there is no "selected type" to cycle through beforehand.
 * <ul>
 *   <li>Plain right-click on a lane connector: grow a connection toward the face actually
 *   clicked (PIPE against another same-type pipe, IMPORT against a capability-providing block).</li>
 *   <li>Plain right-click on an existing arm: to another pipe, disconnect it; to a machine,
 *   cycle IMPORT → EXPORT → disconnected.</li>
 *   <li>Shift + right-click any part of a type: remove that whole type, dropping one pipe item
 *   (removing the block entirely if it was the last type present).</li>
 * </ul>
 */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class PipeWrenchEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseBlock() == TriState.FALSE) return;

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack wrench = player.getItemInHand(hand);
        if (!(wrench.getItem() instanceof PipeWrenchItem)) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity pipe)) return;

        BlockHitResult hit = event.getHitVec();
        PipeHitPart part = LogisticsPipeBlock.getHitPart(pipe, hit);
        if (part == null) return;

        if (level instanceof ServerLevel serverLevel) {
            useWrench(pipe, player, wrench, hand, serverLevel, part, hit);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
    }

    private static void useWrench(PipeBlockEntity pipe, Player player, ItemStack wrench, InteractionHand hand,
                                   ServerLevel level, PipeHitPart part, BlockHitResult hit) {
        BlockPos pos = pipe.getBlockPos();
        PipeType type = part.type();

        if (player.isShiftKeyDown()) {
            boolean removeBlock = pipe.presentCount() == 1;
            pipe.removeType(level, type);
            ItemStack drop = new ItemStack(LogisticsPipeBlock.itemFor(type));
            Block.popResource(level, pos, drop);
            if (removeBlock) {
                level.removeBlock(pos, false);
            }
            playBreakSound(level, pos);
            damageWrench(wrench, player, hand);
            return;
        }

        boolean changed = part.direction() == null
                ? growConnection(pipe, level, pos, type, hit.getDirection())
                : editConnection(pipe, level, pos, type, part.direction());
        if (changed) {
            damageWrench(wrench, player, hand);
        }
    }

    private static void damageWrench(ItemStack wrench, Player player, InteractionHand hand) {
        wrench.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    }

    private static boolean growConnection(PipeBlockEntity pipe, ServerLevel level, BlockPos pos, PipeType type, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        boolean neighborIsPipe = level.getBlockEntity(neighborPos) instanceof PipeBlockEntity np && np.hasType(type);

        if (neighborIsPipe) {
            pipe.setConnection(level, type, dir, PipeConnection.PIPE);
            if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity np) {
                np.setConnection(level, type, dir.getOpposite(), PipeConnection.PIPE);
            }
            playPlaceSound(level, pos);
            return true;
        } else if (PipeBlockEntity.neighborHasCapability(type, level, neighborPos, dir.getOpposite())) {
            pipe.setConnection(level, type, dir, PipeConnection.IMPORT);
            playPlaceSound(level, pos);
            return true;
        } else {
            // Nothing to connect to on that face — a distinct, quiet "no-op" cue so a click that
            // registered but changed nothing doesn't look identical to a click that missed the
            // pipe block entirely. No durability lost, since nothing was actually wrenched.
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.25f, 0.6f);
            return false;
        }
    }

    private static boolean editConnection(PipeBlockEntity pipe, ServerLevel level, BlockPos pos, PipeType type, Direction dir) {
        PipeConnection current = pipe.getConnection(type, dir);
        switch (current) {
            case PIPE -> {
                pipe.setConnection(level, type, dir, PipeConnection.NONE);
                BlockPos neighborPos = pos.relative(dir);
                if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity np) {
                    np.setConnection(level, type, dir.getOpposite(), PipeConnection.NONE);
                }
                playBreakSound(level, pos);
                return true;
            }
            case IMPORT -> {
                pipe.setConnection(level, type, dir, PipeConnection.EXPORT);
                playPlaceSound(level, pos);
                return true;
            }
            case EXPORT -> {
                pipe.setConnection(level, type, dir, PipeConnection.NONE);
                playBreakSound(level, pos);
                return true;
            }
            default -> {
                // Not reachable: getHitParts() only ever reports an arm part for an existing connection.
                return false;
            }
        }
    }

    private static void playPlaceSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.6f, 1.0f);
    }

    private static void playBreakSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 1.0f);
    }
}
