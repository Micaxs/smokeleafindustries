package net.micaxs.smokeleaf.block.entity.energy;

import net.minecraftforge.energy.EnergyStorage;
public abstract class ModEnergyStorage extends EnergyStorage {
    public ModEnergyStorage(int capacity, int maxTrensfer) {
        super(capacity, maxTrensfer);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
       int extractedEnergy = super.extractEnergy(toExtract, simulate);
       if (extractedEnergy != 0) {
           onEnergyChanged();
       }
       return extractedEnergy;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int receivedEnergy = super.receiveEnergy(toReceive, simulate);
        if (receivedEnergy != 0) {
            onEnergyChanged();
        }
        return receivedEnergy;
    }

    public int setEnergy(int energy) {
        this.energy = energy;
        return energy;
    }

    public abstract void onEnergyChanged();


}
