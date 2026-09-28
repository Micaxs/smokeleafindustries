package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.GeneratorBlockEntity;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
public class GeneratorMenu extends AbstractContainerMenu {

    public final GeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public GeneratorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public GeneratorMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
       super(ModMenuTypes.GENERATOR_MENU.get(), containerId);
       //checkContainerSize(inv, 2);
       this.blockEntity = ((GeneratorBlockEntity) blockEntity);
       this.level = inv.player.level();
       this.data = data;

       addPlayerInventory(inv);
       addPlayerHotbar(inv);

       this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 37, 34));

       addDataSlots(data);
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
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        final int PLAYER_INV_START = 0;
        final int PLAYER_INV_END = PLAYER_INV_START + PLAYER_INVENTORY_SLOT_COUNT - 1; // 0-26
        final int HOTBAR_START = PLAYER_INV_END + 1;                                   // 27
        final int HOTBAR_END = HOTBAR_START + HOTBAR_SLOT_COUNT - 1;                   // 35
        final int TE_START = TE_INVENTORY_FIRST_SLOT_INDEX;                            // 36
        final int TE_END = TE_START + TE_INVENTORY_SLOT_COUNT - 1;                     // 36

        // Clicked in Tile Entity inventory -> move to player inventory (main then hotbar)
        if (index >= TE_START && index <= TE_END) {
            if (!moveItemStackTo(sourceStack, PLAYER_INV_START, HOTBAR_END + 1, true)) {
                return ItemStack.EMPTY;
            }
        } else { // Clicked in player inventory / hotbar
            boolean insertedInTE = false;

            // Try machine slot first if valid
            if (blockEntity.itemHandler.isItemValid(0, sourceStack)) {
                if (moveItemStackTo(sourceStack, TE_START, TE_END + 1, false)) {
                    insertedInTE = true;
                }
            }

            if (!insertedInTE) {
                // Not inserted into TE, so toggle between main inventory and hotbar like vanilla
                if (index >= PLAYER_INV_START && index <= PLAYER_INV_END) {
                    if (!moveItemStackTo(sourceStack, HOTBAR_START, HOTBAR_END + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= HOTBAR_START && index <= HOTBAR_END) {
                    if (!moveItemStackTo(sourceStack, PLAYER_INV_START, PLAYER_INV_END + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.GENERATOR.get());
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



    public int getFuel() {
        return this.data.get(0);
    }

    public int getMaxFuel() {
        return this.data.get(1);
    }

    public int getFuelStoredScaled() {
        int fuel = this.getFuel();
        int maxFuel = this.getMaxFuel();
        if (fuel > 0 && maxFuel > 0) {
            return (fuel * 14) / maxFuel;
        } else {
            return 0;
        }
    }
}
