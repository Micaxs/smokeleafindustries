package net.micaxs.smokeleaf.block.entity.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;


public class ModEnergyUtil {

    public static boolean move(BlockPos from, BlockPos to, int amount, Level level) {
        EnergyHandler fromStorage = level.getCapability(Capabilities.Energy.BLOCK, from, null);
        EnergyHandler toStorage = level.getCapability(Capabilities.Energy.BLOCK, to, null);
        return EnergyHandlerUtil.move(fromStorage, toStorage, amount, null) > 0;
    }

    public static boolean doesBlockHaveEnergyStorage(BlockPos positionToCheck, Level level) {
        return level.getBlockEntity(positionToCheck) != null && level.getCapability(Capabilities.Energy.BLOCK, positionToCheck, null) != null;
    }

}
