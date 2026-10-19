package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/** Energy analog of {@link PipeEndpointItemHandler} — see its javadoc. Pure forwarding, no buffer. */
public final class PipeEndpointEnergyStorage implements EnergyHandler {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointEnergyStorage(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Nullable
    private PipeNetwork network() {
        if (!(pipe.getLevel() instanceof ServerLevel level)) return null;
        return PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ENERGY);
    }

    private boolean isSelf(PipeEndpoint endpoint) {
        return endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction;
    }

    @Override
    public long getAmountAsLong() {
        return 0;
    }

    @Override
    public long getCapacityAsLong() {
        return 0;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (amount <= 0 || network == null) return 0;

        int remaining = amount;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            if (!(endpoint.cache.getCapability() instanceof EnergyHandler handler)) continue;
            remaining -= handler.insert(remaining, transaction);
            if (remaining <= 0) break;
        }
        return amount - remaining;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        PipeNetwork network = network();
        if (amount <= 0 || network == null) return 0;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (isSelf(endpoint)) continue;
            if (!(endpoint.cache.getCapability() instanceof EnergyHandler handler)) continue;
            int extracted = handler.extract(amount, transaction);
            if (extracted > 0) return extracted;
        }
        return 0;
    }
}
