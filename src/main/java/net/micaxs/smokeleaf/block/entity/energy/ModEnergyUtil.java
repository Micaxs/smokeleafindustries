package net.micaxs.smokeleaf.block.entity.energy;

import net.micaxs.smokeleaf.utils.CapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
public class ModEnergyUtil {

    public static boolean move(BlockPos from, BlockPos to, int amount, Level level) {
        IEnergyStorage fromStorage = CapHelper.get(level, from, null, ForgeCapabilities.ENERGY);
        IEnergyStorage toStorage = CapHelper.get(level, to, null, ForgeCapabilities.ENERGY);

        if (canEnergyStorageExtractThisAmount(fromStorage, amount)) {
            return false;
        }

        if (canEnergyStorageReceiveThisAmount(toStorage, amount)) {
            return false;
        }

        int maxAmountToReceive = toStorage.receiveEnergy(amount, true);
        int extractedEnergy = fromStorage.extractEnergy(maxAmountToReceive, false);
        toStorage.receiveEnergy(extractedEnergy, false);

        return true;
    }



    private static boolean canEnergyStorageReceiveThisAmount(IEnergyStorage toStorage, int amount) {
        return toStorage.getEnergyStored() >= toStorage.getMaxEnergyStored() || !toStorage.canReceive();
    }

    private static boolean canEnergyStorageExtractThisAmount(IEnergyStorage fromStorage, int amount) {
        return fromStorage.getEnergyStored() <= 0 || fromStorage.getEnergyStored() < amount || !fromStorage.canExtract();
    }

    public static boolean doesBlockHaveEnergyStorage(BlockPos positionToCheck, Level level) {
        return level.getBlockEntity(positionToCheck) != null && CapHelper.get(level, positionToCheck, null, ForgeCapabilities.ENERGY) != null;
    }

}

