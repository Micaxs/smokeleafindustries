package net.micaxs.smokeleaf.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Capability glue for 1.20.1: block entities keep their per-side getters (what the 1.21
 * RegisterCapabilitiesEvent providers pointed at) and expose them through {@link #of}.
 */
public final class CapHelper {
    private CapHelper() {}

    /**
     * Resolves {@code cap} for {@code side} against the given per-side getters (any may be null).
     * Returns {@link LazyOptional#empty()} when the getter is absent or returns null.
     */
    public static <T> LazyOptional<T> of(Capability<T> cap, @Nullable Direction side,
                                         @Nullable Function<Direction, ? extends IItemHandler> items,
                                         @Nullable Function<Direction, ? extends IFluidHandler> fluids,
                                         @Nullable Function<Direction, ? extends IEnergyStorage> energy) {
        Object handler = null;
        if (cap == ForgeCapabilities.ITEM_HANDLER && items != null) handler = items.apply(side);
        else if (cap == ForgeCapabilities.FLUID_HANDLER && fluids != null) handler = fluids.apply(side);
        else if (cap == ForgeCapabilities.ENERGY && energy != null) handler = energy.apply(side);
        if (handler == null) return LazyOptional.empty();
        final Object h = handler;
        return LazyOptional.of(() -> h).cast();
    }

    /** 1.21 {@code level.getCapability(cap, pos, side)} equivalent for block capabilities. */
    @Nullable
    public static <T> T get(Level level, BlockPos pos, @Nullable Direction side, Capability<T> cap) {
        if (level == null || !level.isLoaded(pos)) return null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return null;
        return be.getCapability(cap, side).orElse(null);
    }
}
