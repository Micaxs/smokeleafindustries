package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Energy analog of {@link PipeEndpointItemHandler} — see its javadoc. Pure forwarding, no buffer. */
public final class PipeEndpointEnergyStorage implements IEnergyStorage {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointEnergyStorage(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0 || !(pipe.getLevel() instanceof ServerLevel level)) return 0;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ENERGY);
        if (network == null) return 0;

        int remaining = maxReceive;
        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IEnergyStorage storage) || !storage.canReceive()) continue;
            int accepted = storage.receiveEnergy(remaining, simulate);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
        return maxReceive - remaining;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0 || !(pipe.getLevel() instanceof ServerLevel level)) return 0;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.ENERGY);
        if (network == null) return 0;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IEnergyStorage storage) || !storage.canExtract()) continue;
            int extracted = storage.extractEnergy(maxExtract, simulate);
            if (extracted > 0) return extracted;
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
