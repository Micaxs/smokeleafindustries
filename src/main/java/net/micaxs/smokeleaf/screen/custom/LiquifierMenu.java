package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.LiquifierBlockEntity;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquifierMenu extends AbstractContainerMenu {
    public final LiquifierBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public static final int BUTTON_FILL_FROM_BUCKET = 0; // Left Click
    public static final int BUTTON_DRAIN_TO_BUCKET = 1; // Right Click


    public LiquifierMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, validateBlockEntity(inv.player.level().getBlockEntity(extraData.readBlockPos())), new SimpleContainerData(2));
    }

    private static BlockEntity validateBlockEntity(BlockEntity be) {
        if (!(be instanceof LiquifierBlockEntity liquifier)) {
            throw new IllegalStateException("BlockEntity is not a LiquifierBlockEntity!");
        }
        return liquifier;
    }

    public LiquifierMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.LIQUIFIER_MENU.get(), containerId);
        //checkContainerSize(inv, 2);
        this.blockEntity = ((LiquifierBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 30, 35));

        addDataSlots(data);
    }


    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 54; // This is the height in pixels of your arrow
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_FILL_FROM_BUCKET) {
            return tryEmptyContainerIntoTank(player);
        } else if (id == BUTTON_DRAIN_TO_BUCKET) {
            return tryFillContainerFromTank(player);
        }
        return false;
    }

    // Try order: carried -> main hand -> offhand
    private boolean tryEmptyContainerIntoTank(Player player) {
        IFluidHandler tank = blockEntity.getTank(null);

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

        // Main hand
        if (tryEmptyHandIntoTank(player, InteractionHand.MAIN_HAND, tank)) return true;

        // Offhand
        if (tryEmptyHandIntoTank(player, InteractionHand.OFF_HAND, tank)) return true;

        return false;
    }

    private boolean tryFillContainerFromTank(Player player) {
        IFluidHandler tank = blockEntity.getTank(null);

        // Carried (mouse cursor)
        ItemStack carried = getCarried();
        if (!carried.isEmpty()) {
            if (carried.getCount() > 1 && carried.is(Items.BUCKET)) {
                FluidStack simulated = tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                if (simulated.isEmpty() || simulated.getAmount() < 1000) return false;

                ItemStack filledBucket = filledBucketForFluid(simulated);
                if (filledBucket.isEmpty()) return false;

                FluidStack drained = tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty() || drained.getAmount() < 1000) return false;

                ItemStack remainder = carried.copy();
                remainder.shrink(1);
                setCarried(remainder);

                Inventory inv = player.getInventory();
                if (!inv.add(filledBucket)) {
                    player.drop(filledBucket, false);
                }

                inv.setChanged();
                player.inventoryMenu.broadcastChanges();
                blockEntity.setChanged();
                broadcastChanges();
                return true;
            }

            FluidActionResult res = FluidUtil.tryFillContainer(carried, tank, 1000, player, true);
            if (res.isSuccess()) {
                setCarried(res.getResult());
                blockEntity.setChanged();
                broadcastChanges();
                return true;
            }
        }

        // Main hand
        if (tryFillHandFromTank(player, InteractionHand.MAIN_HAND, tank)) return true;

        // Offhand
        if (tryFillHandFromTank(player, InteractionHand.OFF_HAND, tank)) return true;

        return false;
    }

    private boolean tryEmptyHandIntoTank(Player player, InteractionHand hand, IFluidHandler tank) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return false;
        FluidActionResult res = FluidUtil.tryEmptyContainer(held, tank, 1000, player, true);
        if (res.isSuccess()) {
            player.setItemInHand(hand, res.getResult());
            blockEntity.setChanged();
            broadcastChanges();
            return true;
        }
        return false;
    }

    private boolean tryFillHandFromTank(Player player, InteractionHand hand, IFluidHandler tank) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return false;

        // STACKED empty buckets: manual drain + insert filled bucket.
        // This must short-circuit the entire method to prevent any later logic replacing the hand.
        if (held.getCount() > 1 && held.is(Items.BUCKET)) {
            Inventory inv = player.getInventory();

            FluidStack simulated = tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
            if (simulated.isEmpty() || simulated.getAmount() < 1000) return false;

            ItemStack filledBucket = filledBucketForFluid(simulated);
            if (filledBucket.isEmpty()) return false;

            FluidStack drained = tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            if (drained.isEmpty() || drained.getAmount() < 1000) return false;

            // Deduct exactly 1 empty bucket from the actual slot
            if (hand == InteractionHand.MAIN_HAND) {
                int selected = inv.selected;
                ItemStack inSlot = inv.getItem(selected);
                // Be strict here: only mutate if it is still our empty bucket stack
                if (!inSlot.is(Items.BUCKET) || inSlot.getCount() <= 1) return false;
                inSlot.shrink(1);
                inv.setItem(selected, inSlot);
            } else {
                ItemStack inSlot = inv.offhand.get(0);
                if (!inSlot.is(Items.BUCKET) || inSlot.getCount() <= 1) return false;
                inSlot.shrink(1);
                inv.offhand.set(0, inSlot);
            }

            // Add filled bucket to inventory, otherwise drop
            if (!inv.add(filledBucket)) {
                player.drop(filledBucket, false);
            }

            // Force sync
            inv.setChanged();
            player.inventoryMenu.broadcastChanges();
            blockEntity.setChanged();
            broadcastChanges();
            return true;
        }

        // Single bucket / other containers (including cursor-carried stack handled elsewhere)
        FluidActionResult res = FluidUtil.tryFillContainer(held, tank, 1000, player, true);
        if (res.isSuccess()) {
            player.setItemInHand(hand, res.getResult());
            blockEntity.setChanged();
            broadcastChanges();
            return true;
        }
        return false;
    }

    private static ItemStack filledBucketForFluid(FluidStack drained) {
        Fluid fluid = drained.getFluid();
        Item bucketItem = fluid.getBucket();
        return new ItemStack(bucketItem);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.LIQUIFIER.get());
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
