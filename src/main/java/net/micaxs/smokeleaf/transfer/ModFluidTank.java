package net.micaxs.smokeleaf.transfer;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Predicate;

/**
 * Single-tank fluid storage built on NeoForge's transfer API ({@link FluidStacksResourceHandler}),
 * exposed directly as the fluid capability, with the {@code FluidTank}-style convenience methods
 * the machines use internally.
 */
public class ModFluidTank extends FluidStacksResourceHandler {
    private final Predicate<FluidStack> validator;

    public ModFluidTank(int capacity) {
        this(capacity, stack -> true);
    }

    public ModFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(1, capacity);
        this.validator = validator;
    }

    /** Returns the live stack in the tank. */
    public FluidStack getFluid() {
        return stacks.get(0);
    }

    public int getFluidAmount() {
        return stacks.get(0).getAmount();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSpace() {
        return Math.max(0, capacity - getFluidAmount());
    }

    public boolean isEmpty() {
        return stacks.get(0).isEmpty();
    }

    /**
     * Overwrites the tank's contents directly, without firing {@link #onContentsChanged()} — same as
     * the old {@code FluidTank#setFluid}.
     */
    public void setFluid(FluidStack stack) {
        stacks.set(0, stack);
    }

    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int filled = insert(0, FluidResource.of(resource), resource.getAmount(), tx);
            if (action.execute()) tx.commit();
            return filled;
        }
    }

    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = stacks.get(0);
        if (maxDrain <= 0 || current.isEmpty()) return FluidStack.EMPTY;
        return drain(current.copyWithAmount(maxDrain), action);
    }

    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        FluidResource fluid = FluidResource.of(resource);
        try (Transaction tx = Transaction.openRoot()) {
            int drained = extract(0, fluid, resource.getAmount(), tx);
            if (action.execute()) tx.commit();
            return drained > 0 ? fluid.toStack(drained) : FluidStack.EMPTY;
        }
    }

    protected void onContentsChanged() {
    }

    @Override
    public final boolean isValid(int index, FluidResource resource) {
        return resource.isEmpty() || isFluidValid(resource.toStack(1));
    }

    @Override
    protected final void onContentsChanged(int index, FluidStack previousContents) {
        onContentsChanged();
    }
}
