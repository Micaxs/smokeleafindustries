package net.micaxs.smokeleaf.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.IntPredicate;

/**
 * Wraps a machine's real {@link IItemHandler} for the capability it exposes to outside neighbors
 * (pipes, hoppers, other automation), restricting {@link #extractItem} to a caller-supplied set of
 * slots while leaving insertion (and therefore each slot's own {@code isItemValid} gating) untouched.
 *
 * <p>Every machine's internal {@code ItemStackHandler} already stops the wrong item from being
 * inserted into the wrong slot (e.g. only the input slot accepts a valid recipe ingredient), but
 * {@code ItemStackHandler#extractItem} has no equivalent per-slot restriction — a pipe's IMPORT
 * connection (pulling items out) could just as easily pull straight out of an input slot as an
 * output one, stealing an unprocessed ingredient before the machine ever runs. Wrapping the exposed
 * capability with this class (instead of returning the raw handler directly from
 * {@code getItemHandler(Direction)}) is what fixes that: only the slots passed as {@code extractable}
 * can ever be pulled from externally, while the machine's own internal crafting code keeps using the
 * unwrapped handler directly and is unaffected.
 */
public class ExtractRestrictedItemHandler implements IItemHandler {
    private final IItemHandler delegate;
    private final IntPredicate extractable;

    public ExtractRestrictedItemHandler(IItemHandler delegate, IntPredicate extractable) {
        this.delegate = delegate;
        this.extractable = extractable;
    }

    /** Convenience for the common case of a single dedicated output slot. */
    public static ExtractRestrictedItemHandler outputOnly(IItemHandler delegate, int outputSlot) {
        return new ExtractRestrictedItemHandler(delegate, slot -> slot == outputSlot);
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!extractable.test(slot)) return ItemStack.EMPTY;
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return delegate.isItemValid(slot, stack);
    }
}
