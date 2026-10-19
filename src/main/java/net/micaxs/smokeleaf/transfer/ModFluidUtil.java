package net.micaxs.smokeleaf.transfer;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

/**
 * Container (bucket, tincture, ...) helpers on top of NeoForge's transfer API, replacing the
 * deprecated {@code net.neoforged.neoforge.fluids.FluidUtil} container methods the menus and
 * machines used. Each operation works on a single item of the given stack, like the old methods did.
 */
public final class ModFluidUtil {
    private ModFluidUtil() {}

    /** The first fluid contained in the item, or {@link FluidStack#EMPTY}. */
    public static FluidStack getFluidContained(ItemStack stack) {
        if (stack.isEmpty()) return FluidStack.EMPTY;
        return FluidUtil.getFirstStackContained(stack);
    }

    /** Pours up to {@code maxAmount} from one item of {@code container} into {@code tank}. */
    public static FluidActionResult tryEmptyContainer(ItemStack container, ResourceHandler<FluidResource> tank, int maxAmount,
                                                      @Nullable Player player, boolean doTransfer) {
        return transfer(container, tank, maxAmount, doTransfer, true);
    }

    /** Fills one item of {@code container} with up to {@code maxAmount} from {@code tank}. */
    public static FluidActionResult tryFillContainer(ItemStack container, ResourceHandler<FluidResource> tank, int maxAmount,
                                                     @Nullable Player player, boolean doTransfer) {
        return transfer(container, tank, maxAmount, doTransfer, false);
    }

    /** Fills {@code handler} with {@code stack}; returns the amount accepted. */
    public static int fill(ResourceHandler<FluidResource> handler, FluidStack stack, FluidAction action) {
        if (stack.isEmpty()) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int filled = handler.insert(FluidResource.of(stack), stack.getAmount(), tx);
            if (action.execute()) tx.commit();
            return filled;
        }
    }

    /** Drains up to {@code maxAmount} of whatever fluid {@code handler} offers first. */
    public static FluidStack drain(ResourceHandler<FluidResource> handler, int maxAmount, FluidAction action) {
        if (maxAmount <= 0) return FluidStack.EMPTY;
        try (Transaction tx = Transaction.openRoot()) {
            var extracted = ResourceHandlerUtil.extractFirst(handler, fluid -> true, maxAmount, tx);
            if (extracted == null) return FluidStack.EMPTY;
            if (action.execute()) tx.commit();
            return extracted.resource().toStack(extracted.amount());
        }
    }

    /** Filled bucket for a drained stack, keeping its data components (via {@code FluidType#getBucket}). */
    public static ItemStack getFilledBucket(FluidStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        return stack.getFluidType().getBucket(stack);
    }

    private static FluidActionResult transfer(ItemStack container, ResourceHandler<FluidResource> tank, int maxAmount,
                                              boolean doTransfer, boolean intoTank) {
        if (container.isEmpty() || tank == null) return FluidActionResult.FAILURE;

        // A one-slot scratch inventory holding a single item: the item's fluid capability swaps the
        // item in place (e.g. water bucket -> empty bucket), so afterwards the slot holds the result.
        ItemStacksResourceHandler holder = new ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, container.copyWithCount(1)));
        ResourceHandler<FluidResource> itemFluid = ItemAccess.forHandlerIndex(holder, 0).getCapability(Capabilities.Fluid.ITEM);
        if (itemFluid == null) return FluidActionResult.FAILURE;

        try (Transaction tx = Transaction.openRoot()) {
            int moved = intoTank
                    ? ResourceHandlerUtil.move(itemFluid, tank, fluid -> true, maxAmount, tx)
                    : ResourceHandlerUtil.move(tank, itemFluid, fluid -> true, maxAmount, tx);
            if (moved <= 0) return FluidActionResult.FAILURE;

            ItemStack result = holder.getResource(0).toStack(holder.getAmountAsInt(0));
            if (doTransfer) tx.commit();
            return new FluidActionResult(result);
        }
    }
}
