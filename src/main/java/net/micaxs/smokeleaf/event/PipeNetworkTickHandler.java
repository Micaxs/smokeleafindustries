package net.micaxs.smokeleaf.event;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.entity.pipe.PipeEndpoint;
import net.micaxs.smokeleaf.block.entity.pipe.PipeNetwork;
import net.micaxs.smokeleaf.block.entity.pipe.PipeNetworkManager;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * The single driver of all pipe transfer logic. Iterates only the endpoint-bearing pipe positions
 * tracked by {@link PipeNetworkManager} (never every pipe block), dedupes them into their unique
 * cached {@link PipeNetwork}s, and moves items/fluid/energy between EXPORT and IMPORT endpoints
 * instantly (no item entities, no travel animation — see the pipe system plan for why this is both
 * what was asked for visually and what keeps long runs cheap).
 */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public final class PipeNetworkTickHandler {

    private static final int ITEM_FLUID_INTERVAL_TICKS = 4;
    private static final int ITEM_TRANSFER_CAP = 32;
    private static final int FLUID_TRANSFER_CAP = 1000; // 1 bucket

    private PipeNetworkTickHandler() {}

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        PipeNetworkManager manager = PipeNetworkManager.get(level);
        long gameTime = level.getGameTime();

        tickEnergy(level, manager);
        tickThrottled(level, manager, PipeType.ITEM, gameTime, PipeNetworkTickHandler::transferItems);
        tickThrottled(level, manager, PipeType.FLUID, gameTime, PipeNetworkTickHandler::transferFluids);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PipeNetworkManager.clearAll();
    }

    // -----------------------------------------------------------------------
    // Network gathering (O(endpoint-bearing pipes), never O(all pipes))
    // -----------------------------------------------------------------------

    private static Set<PipeNetwork> gatherActiveNetworks(ServerLevel level, PipeNetworkManager manager, PipeType type) {
        Set<BlockPos> bearing = manager.getEndpointBearingPositions(type);
        if (bearing.isEmpty()) return Collections.emptySet();
        Set<PipeNetwork> networks = Collections.newSetFromMap(new IdentityHashMap<>());
        for (BlockPos pos : bearing) {
            PipeNetwork network = manager.getOrBuild(level, pos, type);
            if (network != null) networks.add(network);
        }
        return networks;
    }

    private interface Transfer {
        void run(PipeNetwork network);
    }

    /** Every active network is checked every tick, but each network only actually transfers on its own phase (1 in every {@link #ITEM_FLUID_INTERVAL_TICKS} ticks), spreading load instead of spiking it. */
    private static void tickThrottled(ServerLevel level, PipeNetworkManager manager, PipeType type, long gameTime, Transfer transfer) {
        for (PipeNetwork network : gatherActiveNetworks(level, manager, type)) {
            if ((network.hashCode() & (ITEM_FLUID_INTERVAL_TICKS - 1)) != (int) (gameTime & (ITEM_FLUID_INTERVAL_TICKS - 1))) continue;
            transfer.run(network);
        }
    }

    private static void tickEnergy(ServerLevel level, PipeNetworkManager manager) {
        for (PipeNetwork network : gatherActiveNetworks(level, manager, PipeType.ENERGY)) {
            transferEnergy(network);
        }
    }

    // -----------------------------------------------------------------------
    // Items
    // -----------------------------------------------------------------------

    private static void transferItems(PipeNetwork network) {
        List<PipeEndpoint> exports = network.exports();
        List<PipeEndpoint> imports = network.imports();
        if (exports.isEmpty() || imports.isEmpty()) return;

        if (network.roundRobinCursor >= exports.size()) network.roundRobinCursor = 0;
        PipeEndpoint export = exports.get(network.roundRobinCursor);
        network.roundRobinCursor = (network.roundRobinCursor + 1) % exports.size();

        if (!(export.cache.getCapability() instanceof IItemHandler source)) return;

        int remaining = ITEM_TRANSFER_CAP;
        for (PipeEndpoint imp : imports) {
            if (sameFace(export, imp)) continue;
            if (!(imp.cache.getCapability() instanceof IItemHandler dest)) continue;
            remaining -= moveItems(source, dest, remaining);
            if (remaining <= 0) break;
        }
    }

    private static int moveItems(IItemHandler source, IItemHandler dest, int maxAmount) {
        int moved = 0;
        for (int slot = 0; slot < source.getSlots() && moved < maxAmount; slot++) {
            ItemStack peek = source.extractItem(slot, maxAmount - moved, true);
            if (peek.isEmpty()) continue;

            ItemStack simulatedLeftover = ItemHandlerHelper.insertItemStacked(dest, peek, true);
            int insertable = peek.getCount() - simulatedLeftover.getCount();
            if (insertable <= 0) continue;

            ItemStack extracted = source.extractItem(slot, insertable, false);
            if (extracted.isEmpty()) continue;

            ItemStack notInserted = ItemHandlerHelper.insertItemStacked(dest, extracted, false);
            int actuallyMoved = extracted.getCount() - notInserted.getCount();
            if (!notInserted.isEmpty()) {
                // Insert didn't fully take (shouldn't normally happen given the simulate above) — put it back rather than lose it.
                ItemHandlerHelper.insertItemStacked(source, notInserted, false);
            }
            moved += actuallyMoved;
        }
        return moved;
    }

    // -----------------------------------------------------------------------
    // Fluids
    // -----------------------------------------------------------------------

    private static void transferFluids(PipeNetwork network) {
        List<PipeEndpoint> exports = network.exports();
        List<PipeEndpoint> imports = network.imports();
        if (exports.isEmpty() || imports.isEmpty()) return;

        if (network.roundRobinCursor >= exports.size()) network.roundRobinCursor = 0;
        PipeEndpoint export = exports.get(network.roundRobinCursor);
        network.roundRobinCursor = (network.roundRobinCursor + 1) % exports.size();

        if (!(export.cache.getCapability() instanceof IFluidHandler source)) return;
        if (source.getFluidInTank(0).getFluid() == Fluids.EMPTY) return;

        for (PipeEndpoint imp : imports) {
            if (sameFace(export, imp)) continue;
            if (!(imp.cache.getCapability() instanceof IFluidHandler dest)) continue;
            FluidStack moved = FluidUtil.tryFluidTransfer(dest, source, FLUID_TRANSFER_CAP, true);
            if (!moved.isEmpty()) break;
        }
    }

    // -----------------------------------------------------------------------
    // Energy — every tick, every export, no internal buffer (pure forwarding, see plan)
    // -----------------------------------------------------------------------

    private static void transferEnergy(PipeNetwork network) {
        List<PipeEndpoint> exports = network.exports();
        List<PipeEndpoint> imports = network.imports();
        if (exports.isEmpty() || imports.isEmpty()) return;

        for (PipeEndpoint export : exports) {
            if (!(export.cache.getCapability() instanceof IEnergyStorage source) || !source.canExtract()) continue;
            int available = source.extractEnergy(Integer.MAX_VALUE, true);
            if (available <= 0) continue;

            int remaining = available;
            for (PipeEndpoint imp : imports) {
                if (sameFace(export, imp)) continue;
                if (!(imp.cache.getCapability() instanceof IEnergyStorage dest) || !dest.canReceive()) continue;
                int accepted = dest.receiveEnergy(remaining, false);
                if (accepted > 0) {
                    source.extractEnergy(accepted, false);
                    remaining -= accepted;
                }
                if (remaining <= 0) break;
            }
        }
    }

    private static boolean sameFace(PipeEndpoint a, PipeEndpoint b) {
        return a.pipePos.equals(b.pipePos) && a.direction == b.direction;
    }
}
