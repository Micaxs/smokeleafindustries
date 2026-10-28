package net.micaxs.smokeleaf.block.entity.pipe;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Splits energy evenly across several receivers instead of topping up the first one before the next
 * sees anything. Receivers that take less than their share (full, or rate-limited) drop out and their
 * leftover is re-split among the rest, so no energy is stranded while anyone can still accept it.
 */
public final class EnergyDistribution {

    private EnergyDistribution() {}

    /** @return the total amount accepted across all {@code targets}. */
    public static int distribute(List<IEnergyStorage> targets, int amount, boolean simulate) {
        if (amount <= 0 || targets.isEmpty()) return 0;

        if (simulate) {
            // Simulated receives don't change state, so repeated even-split passes would over-count;
            // the total a set of receivers can take doesn't depend on order anyway.
            int remaining = amount;
            for (IEnergyStorage target : targets) {
                remaining -= target.receiveEnergy(remaining, true);
                if (remaining <= 0) break;
            }
            return amount - remaining;
        }

        List<IEnergyStorage> open = new ArrayList<>(targets);
        int remaining = amount;
        while (remaining > 0 && !open.isEmpty()) {
            int share = Math.max(1, remaining / open.size());
            Iterator<IEnergyStorage> it = open.iterator();
            while (it.hasNext() && remaining > 0) {
                int offer = Math.min(share, remaining);
                int accepted = it.next().receiveEnergy(offer, false);
                remaining -= accepted;
                if (accepted < offer) it.remove();
            }
        }
        return amount - remaining;
    }
}
