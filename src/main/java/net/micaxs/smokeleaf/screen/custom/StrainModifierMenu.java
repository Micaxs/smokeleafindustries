package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.StrainModifierBlockEntity;
import net.micaxs.smokeleaf.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class StrainModifierMenu extends AbstractContainerMenu {

    public static final int BUTTON_SAVE = 0;

    public final StrainModifierBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public StrainModifierMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                validateBlockEntity(inv.player.level().getBlockEntity(extraData.readBlockPos())),
                new SimpleContainerData(17));
    }


    public StrainModifierMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.STRAIN_MODIFIER_MENU.get(), containerId);

        this.blockEntity = (StrainModifierBlockEntity) blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Slot positions derived from pixel analysis of strain_modifier_gui.png
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, StrainModifierBlockEntity.SLOT_SEED, 8, 8));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, StrainModifierBlockEntity.SLOT_LEAF, 26, 59));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, StrainModifierBlockEntity.SLOT_OUTPUT, 153, 8));

        addDataSlots(data);
    }


    // Data Getters
    public int     getLeafMeter()   { return data.get(0); }
    public int     getPreviewThc()  { return data.get(1); }
    public int     getPreviewCbd()  { return data.get(2); }
    public int     getLeafColorR()  { return data.get(3); }
    public int     getLeafColorG()  { return data.get(4); }
    public int     getLeafColorB()  { return data.get(5); }
    public int     getStrainColorR(){ return data.get(6); }
    public int     getStrainColorG(){ return data.get(7); }
    public int     getStrainColorB(){ return data.get(8); }
    public int     getOrigThc()     { return data.get(9); }
    public int     getOrigCbd()     { return data.get(10); }
    public int     getOrigLeafR()   { return data.get(11); }
    public int     getOrigLeafG()   { return data.get(12); }
    public int     getOrigLeafB()   { return data.get(13); }
    public int     getOrigStrainR() { return data.get(14); }
    public int     getOrigStrainG() { return data.get(15); }
    public int     getOrigStrainB() { return data.get(16); }
    public String  getStrainName()  { return blockEntity.getStrainName(); }
    public boolean hasSeed()        { return blockEntity.hasSeed(); }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_SAVE) return blockEntity.startCrafting(player.getName().getString());
        return false;
    }




    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!

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
                pPlayer, ModBlocks.STRAIN_MODIFIER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    private static BlockEntity validateBlockEntity(BlockEntity be) {
        if (!(be instanceof StrainModifierBlockEntity))
            throw new IllegalStateException("BlockEntity is not a StrainModifierBlockEntity!");
        return be;
    }
}
