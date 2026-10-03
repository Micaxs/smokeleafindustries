package net.micaxs.smokeleaf.strain;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Tracks currently-loaded, server-side block entities that hold strain-tagged fluid tanks
 * (Mixer, Mutator, Liquifier), so a strain-identity update can patch matching fluids in place
 * instead of requiring a full world scan.
 */
public final class StrainTankTracker {

    private static final Set<StrainTankHolder> ACTIVE = Collections.newSetFromMap(new WeakHashMap<>());

    private StrainTankTracker() {}

    public static void register(StrainTankHolder holder) {
        ACTIVE.add(holder);
    }

    public static void unregister(StrainTankHolder holder) {
        ACTIVE.remove(holder);
    }

    public static void propagate(String strainId, StrainRegistrySavedData.StrainEntry entry) {
        for (StrainTankHolder holder : List.copyOf(ACTIVE)) {
            holder.applyStrainRegistryUpdate(strainId, entry);
        }
    }
}
