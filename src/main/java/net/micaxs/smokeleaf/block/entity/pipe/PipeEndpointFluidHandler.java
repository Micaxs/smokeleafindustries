package net.micaxs.smokeleaf.block.entity.pipe;

import net.micaxs.smokeleaf.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/** Fluid analog of {@link PipeEndpointItemHandler} — see its javadoc. */
public final class PipeEndpointFluidHandler implements IFluidHandler {
    private final PipeBlockEntity pipe;
    private final Direction direction;

    public PipeEndpointFluidHandler(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.direction = direction;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 1000;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !(pipe.getLevel() instanceof ServerLevel level)) return 0;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.FLUID);
        if (network == null) return 0;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IFluidHandler handler)) continue;
            int filled = handler.fill(resource, action);
            if (filled > 0) return filled;
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !(pipe.getLevel() instanceof ServerLevel level)) return FluidStack.EMPTY;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.FLUID);
        if (network == null) return FluidStack.EMPTY;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IFluidHandler handler)) continue;
            FluidStack drained = handler.drain(resource, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || !(pipe.getLevel() instanceof ServerLevel level)) return FluidStack.EMPTY;
        PipeNetwork network = PipeNetworkManager.get(level).getOrBuild(level, pipe.getBlockPos(), PipeType.FLUID);
        if (network == null) return FluidStack.EMPTY;

        for (PipeEndpoint endpoint : network.endpoints) {
            if (endpoint.pipePos.equals(pipe.getBlockPos()) && endpoint.direction == direction) continue;
            if (!(endpoint.cache.getCapability() instanceof IFluidHandler handler)) continue;
            FluidStack drained = handler.drain(maxDrain, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
}
