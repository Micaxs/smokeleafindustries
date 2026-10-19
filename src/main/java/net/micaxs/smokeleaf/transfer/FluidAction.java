package net.micaxs.smokeleaf.transfer;

/**
 * Simulate/execute flag for {@link ModFluidTank}'s convenience fill/drain methods — the mod-local
 * replacement for NeoForge's deprecated {@code IFluidHandler.FluidAction}.
 */
public enum FluidAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
        return this == EXECUTE;
    }

    public boolean simulate() {
        return this == SIMULATE;
    }
}
