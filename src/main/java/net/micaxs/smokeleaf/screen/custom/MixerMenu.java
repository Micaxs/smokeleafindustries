package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.block.entity.MixerBlockEntity;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;

public class MixerMenu extends AbstractContainerMenu {

    public static final int BUTTON_FILL_INPUT_A_FROM_BUCKET = 0; // Left click input A
    public static final int BUTTON_FILL_INPUT_B_FROM_BUCKET = 1; // Left click input B
    public static final int BUTTON_DRAIN_OUTPUT_TO_BUCKET = 2;   // Right click output

    public final MixerBlockEntity blockEntity;
    private final ContainerData data;

    public MixerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, validate(inv.player.level().getBlockEntity(extraData.readBlockPos())), new SimpleContainerData(2));
    }

    private static BlockEntity validate(BlockEntity be) {
        if (!(be instanceof MixerBlockEntity mixer)) {
            throw new IllegalStateException("BlockEntity is not a MixerBlockEntity!");
        }
        return mixer;
    }

    public MixerMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.MIXER_MENU.get(), containerId);
        this.blockEntity = (MixerBlockEntity) blockEntity;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int max = data.get(1);
        int size = 26;
        return max != 0 && progress != 0 ? progress * size / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        itemstack = stack.copy();

        int vanillaSlots = 36;
        int teSlots = 2;
        int teStart = 0;
        int teEnd = teSlots;
        int invStart = teSlots;
        int invEnd = teSlots + vanillaSlots;

        if (index < teSlots) {
            if (!this.moveItemStackTo(stack, invStart, invEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, teStart, teEnd, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return switch (id) {
            case BUTTON_FILL_INPUT_A_FROM_BUCKET -> tryEmptyContainerInto(player, blockEntity.getTankA(null));
            case BUTTON_FILL_INPUT_B_FROM_BUCKET -> tryEmptyContainerInto(player, blockEntity.getTankB(null));
            case BUTTON_DRAIN_OUTPUT_TO_BUCKET -> tryFillContainerFrom(player, blockEntity.getTankOut(null));
            default -> false;
        };
    }

    private boolean tryEmptyContainerInto(Player player, IFluidHandler tank) {
        // Carried (mouse cursor)
        ItemStack carried = getCarried();
        if (!carried.isEmpty()) {
            FluidActionResult res = FluidUtil.tryEmptyContainer(carried, tank, 1000, player, true);
            if (res.isSuccess()) {
                setCarried(res.getResult());
                blockEntity.setChanged();
                broadcastChanges();
                return true;
            }
        }

        if (tryEmptyHandInto(player, InteractionHand.MAIN_HAND, tank)) return true;
        if (tryEmptyHandInto(player, InteractionHand.OFF_HAND, tank)) return true;
        return false;
    }

    private boolean tryFillContainerFrom(Player player, IFluidHandler tank) {
        // Carried (mouse cursor)
        ItemStack carried = getCarried();
        if (!carried.isEmpty()) {
            // Special-case buckets so we can copy strain data from the drained fluid into the filled bucket stack.
            if (carried.is(Items.BUCKET)) {
                ItemStack filled = tryFillSingleBucketFromTank(player, tank, carried);
                if (!filled.isEmpty()) {
                    setCarried(filled);
                    blockEntity.setChanged();
                    broadcastChanges();
                    return true;
                }
            }

            FluidActionResult res = FluidUtil.tryFillContainer(carried, tank, 1000, player, true);
            if (res.isSuccess()) {
                setCarried(res.getResult());
                blockEntity.setChanged();
                broadcastChanges();
                return true;
            }
        }

        if (tryFillHandFrom(player, InteractionHand.MAIN_HAND, tank)) return true;
        if (tryFillHandFrom(player, InteractionHand.OFF_HAND, tank)) return true;
        return false;
    }

    private boolean tryEmptyHandInto(Player player, InteractionHand hand, IFluidHandler handler) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return false;
        FluidActionResult res = FluidUtil.tryEmptyContainer(held, handler, 1000, player, true);
        if (res.isSuccess()) {
            player.setItemInHand(hand, res.getResult());
            blockEntity.setChanged();
            broadcastChanges();
            return true;
        }
        return false;
    }

    private boolean tryFillHandFrom(Player player, InteractionHand hand, IFluidHandler handler) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return false;

        // Special-case buckets so we can copy strain data from the drained fluid into the filled bucket stack.
        if (held.is(Items.BUCKET)) {
            ItemStack filled = tryFillSingleBucketFromTank(player, handler, held);
            if (!filled.isEmpty()) {
                player.setItemInHand(hand, filled);
                blockEntity.setChanged();
                broadcastChanges();
                return true;
            }
        }

        FluidActionResult res = FluidUtil.tryFillContainer(held, handler, 1000, player, true);
        if (res.isSuccess()) {
            player.setItemInHand(hand, res.getResult());
            blockEntity.setChanged();
            broadcastChanges();
            return true;
        }
        return false;
    }

    /**
     * Fills exactly one empty bucket from the given handler and returns the new stack.
     * Returns {@link ItemStack#EMPTY} if not possible.
     */
    private ItemStack tryFillSingleBucketFromTank(Player player, IFluidHandler handler, ItemStack bucketStack) {
        if (!bucketStack.is(Items.BUCKET) || bucketStack.isEmpty()) return ItemStack.EMPTY;

        FluidStack simulated = handler.drain(1000, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty() || simulated.getAmount() < 1000) return ItemStack.EMPTY;

        ItemStack filledBucket = filledBucketForFluid(simulated);
        if (filledBucket.isEmpty()) return ItemStack.EMPTY;

        FluidStack drained = handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty() || drained.getAmount() < 1000) return ItemStack.EMPTY;

        if (bucketStack.getCount() == 1) {
            // Replace the bucket in-place (vanilla feel).
            return filledBucket;
        }

        // Stack case: consume 1 empty bucket and stow/drop the filled one.
        ItemStack remainder = bucketStack.copy();
        remainder.shrink(1);

        Inventory inv = player.getInventory();
        if (!inv.add(filledBucket)) player.drop(filledBucket, false);

        inv.setChanged();
        player.inventoryMenu.broadcastChanges();
        return remainder;
    }

    private static ItemStack filledBucketForFluid(FluidStack drained) {
        Fluid fluid = drained.getFluid();
        Item bucketItem = fluid.getBucket();
        ItemStack stack = new ItemStack(bucketItem);

        StrainData strain = StrainUtil.getStrain(drained);
        if (strain != net.micaxs.smokeleaf.strain.StrainData.EMPTY) {
            StrainUtil.setStrain(stack, strain);
        }

        return stack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
