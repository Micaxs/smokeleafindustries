package net.micaxs.smokeleaf.transfer;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Machine energy buffer built on NeoForge's transfer API ({@link SimpleEnergyHandler}), exposed directly
 * as the energy capability. Keeps the int-based convenience methods the machines use internally.
 */
public abstract class ModEnergyStorage extends SimpleEnergyHandler {
    public ModEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public int getEnergyStored() {
        return energy;
    }

    public int getMaxEnergyStored() {
        return capacity;
    }

    public boolean canExtract() {
        return maxExtract > 0;
    }

    public boolean canReceive() {
        return maxInsert > 0;
    }

    public int extractEnergy(int toExtract, boolean simulate) {
        if (toExtract <= 0) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = extract(toExtract, tx);
            if (!simulate) tx.commit();
            return extracted;
        }
    }

    public int receiveEnergy(int toReceive, boolean simulate) {
        if (toReceive <= 0) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int received = insert(toReceive, tx);
            if (!simulate) tx.commit();
            return received;
        }
    }

    /** Sets the stored energy directly, without firing {@link #onEnergyChanged()} (used when loading). */
    public int setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, capacity));
        return this.energy;
    }

    @Override
    protected final void onEnergyChanged(int previousAmount) {
        onEnergyChanged();
    }

    public abstract void onEnergyChanged();
}
