package net.micaxs.smokeleaf.strain;

/**
 * Implemented by block entities that can hold a strain-tagged {@code FluidStack} in a tank
 * (Mixer, Mutator, Liquifier). Lets {@link StrainRegistrySavedData} patch matching fluid
 * in place when a strain's identity is edited (e.g. via the Strain Modifier), without needing
 * to enumerate every loaded block entity in the world.
 */
public interface StrainTankHolder {
    void applyStrainRegistryUpdate(String strainId, StrainRegistrySavedData.StrainEntry entry);
}
