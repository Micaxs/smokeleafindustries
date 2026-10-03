package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Exposed on a pipe's IMPORT/EXPORT item face so external (non-pipe) machines/mods can interact with
 * the whole network through this one face — a live view, never a buffer. On insert/extract it simply
 * tries every *other* endpoint of the same network once; the pipe itself stores nothing.
 */
public final class PipeEndpointItemHandler implements IItemHandler {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointItemHandler(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !(pipe.getLevel() instanceof ServerLevel level)) return stack;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ITEM);
        if (network == null) return stack;

        ItemStack remaining = stack.copy();
        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IItemHandler handler)) continue;
            remaining = net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handler, remaining, simulate);
            if (remaining.isEmpty()) break;
        }
        return remaining;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0 || !(pipe.getLevel() instanceof ServerLevel level)) return ItemStack.EMPTY;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ITEM);
        if (network == null) return ItemStack.EMPTY;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IItemHandler handler)) continue;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extracted = handler.extractItem(i, amount, simulate);
                if (!extracted.isEmpty()) return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
