package net.micaxs.smokeleaf.transfer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Machine inventory built on NeoForge's transfer API ({@link ItemStacksResourceHandler}), so it can be
 * exposed directly as an item capability, while keeping the slot-based convenience methods
 * ({@code getStackInSlot}, {@code insertItem}, ...) the machines' own crafting logic is written against.
 *
 * <p>Subclasses customise behaviour through {@link #isItemValid}, {@link #getSlotLimit} and
 * {@link #onContentsChanged(int)}, mirroring the old {@code ItemStackHandler} hooks.
 */
public class ModItemStackHandler extends ItemStacksResourceHandler {
    public ModItemStackHandler(int size) {
        super(size);
    }

    public int getSlots() {
        return size();
    }

    /** Returns the live stack in the slot. Do not mutate it without calling {@link #setStackInSlot} afterwards. */
    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    /** Overwrites the slot, bypassing {@link #isItemValid} (same as the old {@code ItemStackHandler}). */
    public void setStackInSlot(int slot, ItemStack stack) {
        set(slot, ItemResource.of(stack), stack.isEmpty() ? 0 : stack.getCount());
    }

    /** @return the part of {@code stack} that did not fit */
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        int inserted;
        try (Transaction tx = Transaction.openRoot()) {
            inserted = insert(slot, ItemResource.of(stack), stack.getCount(), tx);
            if (!simulate) tx.commit();
        }
        return stack.copyWithCount(stack.getCount() - inserted);
    }

    /** @return the extracted stack, or {@link ItemStack#EMPTY} if nothing could be extracted */
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack current = stacks.get(slot);
        if (amount <= 0 || current.isEmpty()) return ItemStack.EMPTY;
        ItemResource resource = ItemResource.of(current);
        int extracted;
        try (Transaction tx = Transaction.openRoot()) {
            extracted = extract(slot, resource, amount, tx);
            if (!simulate) tx.commit();
        }
        return extracted > 0 ? resource.toStack(extracted) : ItemStack.EMPTY;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    public int getSlotLimit(int slot) {
        return Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    protected void onContentsChanged(int slot) {
    }

    @Override
    public final boolean isValid(int index, ItemResource resource) {
        return resource.isEmpty() || isItemValid(index, resource.toStack());
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return Math.min(getSlotLimit(index), super.getCapacity(index, resource));
    }

    @Override
    protected final void onContentsChanged(int index, ItemStack previousContents) {
        onContentsChanged(index);
    }
}
