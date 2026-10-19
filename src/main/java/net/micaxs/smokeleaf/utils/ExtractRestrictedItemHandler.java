package net.micaxs.smokeleaf.utils;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntPredicate;

/**
 * Wraps a machine's real item {@link ResourceHandler} for the capability it exposes to outside neighbors
 * (pipes, hoppers, other automation), restricting extraction to a caller-supplied set of
 * slots while leaving insertion (and therefore each slot's own {@code isItemValid} gating) untouched.
 *
 * <p>Every machine's internal handler already stops the wrong item from being
 * inserted into the wrong slot (e.g. only the input slot accepts a valid recipe ingredient), but
 * extraction has no equivalent per-slot restriction — a pipe's IMPORT
 * connection (pulling items out) could just as easily pull straight out of an input slot as an
 * output one, stealing an unprocessed ingredient before the machine ever runs. Wrapping the exposed
 * capability with this class (instead of returning the raw handler directly from
 * {@code getItemHandler(Direction)}) is what fixes that: only the slots passed as {@code extractable}
 * can ever be pulled from externally, while the machine's own internal crafting code keeps using the
 * unwrapped handler directly and is unaffected.
 */
public class ExtractRestrictedItemHandler extends DelegatingResourceHandler<ItemResource> {
    private final IntPredicate extractable;

    public ExtractRestrictedItemHandler(ResourceHandler<ItemResource> delegate, IntPredicate extractable) {
        super(delegate);
        this.extractable = extractable;
    }

    /** Convenience for the common case of a single dedicated output slot. */
    public static ExtractRestrictedItemHandler outputOnly(ResourceHandler<ItemResource> delegate, int outputSlot) {
        return new ExtractRestrictedItemHandler(delegate, slot -> slot == outputSlot);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!extractable.test(index)) return 0;
        return super.extract(index, resource, amount, transaction);
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        int extracted = 0;
        for (int index = 0; index < size() && extracted < amount; index++) {
            extracted += extract(index, resource, amount - extracted, transaction);
        }
        return extracted;
    }
}
