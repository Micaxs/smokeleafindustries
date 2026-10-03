package net.micaxs.smokeleaf.block.entity;

import net.micaxs.smokeleaf.block.entity.energy.ModEnergyStorage;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.item.custom.StrainBookItem;
import net.micaxs.smokeleaf.item.custom.UnidentifiedSeedsItem;
import net.micaxs.smokeleaf.screen.custom.StrainModifierMenu;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainEffectsUtil;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.micaxs.smokeleaf.utils.ExtractRestrictedItemHandler;
import net.micaxs.smokeleaf.utils.StrainModifierCostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StrainModifierBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_SEED   = 0;
    public static final int SLOT_LEAF   = 1;   // leaf fill slot (fills the meter)
    public static final int SLOT_OUTPUT = 2;

    public static final int MAX_LEAF_CAPACITY = 256;

    /** Clamped RGB range – prevents pure black or pure white. */
    public static final int COLOR_MIN = 10;
    public static final int COLOR_MAX = 245;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;
    private final ModEnergyStorage energyStorage = new ModEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            if (getLevel() != null) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
        }
    };

    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case SLOT_SEED   -> isModifiableSeed(stack);
                case SLOT_LEAF   -> stack.is(ModItems.HEMP_LEAF.get());
                case SLOT_OUTPUT -> false;
                default          -> false;
            };
        }
        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_SEED ? 1 : super.getSlotLimit(slot);
        }
    };

    // ContainerData layout (17 entries):
    //  0  leafMeter
    //  1  previewThc
    //  2  previewCbd
    //  3  leafColorR
    //  4  leafColorG
    //  5  leafColorB
    //  6  strainColorR
    //  7  strainColorG
    //  8  strainColorB
    //  9  origThc
    // 10  origCbd
    // 11  origLeafR
    // 12  origLeafG
    // 13  origLeafB
    // 14  origStrainR
    // 15  origStrainG
    // 16  origStrainB
    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case  0 -> leafMeter;
                case  1 -> previewThc;
                case  2 -> previewCbd;
                case  3 -> (previewLeafColor   >> 16) & 0xFF;
                case  4 -> (previewLeafColor   >>  8) & 0xFF;
                case  5 ->  previewLeafColor          & 0xFF;
                case  6 -> (previewStrainColor >> 16) & 0xFF;
                case  7 -> (previewStrainColor >>  8) & 0xFF;
                case  8 ->  previewStrainColor         & 0xFF;
                case  9 -> origThc;
                case 10 -> origCbd;
                case 11 -> origLeafR;
                case 12 -> origLeafG;
                case 13 -> origLeafB;
                case 14 -> origStrainR;
                case 15 -> origStrainG;
                case 16 -> origStrainB;
                default -> 0;
            };
        }
        @Override
        public void set(int i, int v) {
            switch (i) {
                case  0 -> leafMeter = v;
                case  1 -> previewThc = v;
                case  2 -> previewCbd = v;
                case  3 -> previewLeafColor   = (previewLeafColor   & 0xFF00FFFF) | ((v & 0xFF) << 16);
                case  4 -> previewLeafColor   = (previewLeafColor   & 0xFFFF00FF) | ((v & 0xFF) <<  8);
                case  5 -> previewLeafColor   = (previewLeafColor   & 0xFFFFFF00) |  (v & 0xFF);
                case  6 -> previewStrainColor = (previewStrainColor & 0xFF00FFFF) | ((v & 0xFF) << 16);
                case  7 -> previewStrainColor = (previewStrainColor & 0xFFFF00FF) | ((v & 0xFF) <<  8);
                case  8 -> previewStrainColor = (previewStrainColor & 0xFFFFFF00) |  (v & 0xFF);
                case  9 -> origThc    = v;
                case 10 -> origCbd    = v;
                case 11 -> origLeafR  = v;
                case 12 -> origLeafG  = v;
                case 13 -> origLeafB  = v;
                case 14 -> origStrainR = v;
                case 15 -> origStrainG = v;
                case 16 -> origStrainB = v;
                default -> { }
            }
        }
        @Override
        public int getCount() { return 17; }
    };

    // Mutable preview state
    private int leafMeter;
    private int previewThc;
    private int previewCbd;
    private int previewLeafColor   = 0xFF4A7A2E;
    private int previewStrainColor = 0xFFAAEF6F;

    // Original (seed) values for cost calculation
    private int origThc, origCbd;
    private int origLeafR = 0x4A, origLeafG = 0x7A, origLeafB = 0x2E;
    private int origStrainR = 0xAA, origStrainG = 0xEF, origStrainB = 0x6F;

    // Strain name: strainName = the current/original name (also the pre-fill + cost baseline),
    // previewName = the name typed in the UI, applied to the output when crafting finishes.
    private String strainName = "";
    private String previewName = "";

    private ItemStack lastSeedStack = ItemStack.EMPTY;

    public StrainModifierBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STRAIN_MODIFIER_BE.get(), pos, blockState);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction dir) { return this.energyStorage; }

    public IItemHandler getItemHandler(@Nullable Direction dir) {
        // SLOT_SEED and SLOT_LEAF are consumed by the identification process itself — a pipe should
        // only ever be able to pull the finished, identified seed back out of SLOT_OUTPUT.
        return ExtractRestrictedItemHandler.outputOnly(this.itemHandler, SLOT_OUTPUT);
    }

    public int getLeafMeter()          { return leafMeter; }
    public int getPreviewThc()         { return previewThc; }
    public int getPreviewCbd()         { return previewCbd; }
    public int getPreviewLeafColor()   { return previewLeafColor; }
    public int getPreviewStrainColor() { return previewStrainColor; }
    public String getStrainName()      { return strainName; }
    public boolean hasSeed()           { return !itemHandler.getStackInSlot(SLOT_SEED).isEmpty(); }

    /** Resets all preview values from the seed. Called when a new seed is inserted. */
    public void recalcPreview() {
        ItemStack seed = itemHandler.getStackInSlot(SLOT_SEED);
        if (!isModifiableSeed(seed)) {
            origThc = origCbd = 0;
            origLeafR = origLeafG = origLeafB = COLOR_MIN;
            origStrainR = origStrainG = origStrainB = COLOR_MIN;
            previewThc = 0; previewCbd = 0;
            previewLeafColor   = colorFromRgb(origLeafR, origLeafG, origLeafB);
            previewStrainColor = colorFromRgb(origStrainR, origStrainG, origStrainB);
            strainName = "";
            previewName = "";
            return;
        }
        StrainData strain = StrainUtil.getStrain(seed);
        origThc = Math.max(0, Math.min(35, strain.thc()));
        origCbd = Math.max(0, Math.min(30, strain.cbd()));
        int lc = strain.leafColor();
        origLeafR = clampColor((lc >> 16) & 0xFF);
        origLeafG = clampColor((lc >>  8) & 0xFF);
        origLeafB = clampColor( lc        & 0xFF);
        int sc = strain.colorArgb();
        origStrainR = clampColor((sc >> 16) & 0xFF);
        origStrainG = clampColor((sc >>  8) & 0xFF);
        origStrainB = clampColor( sc        & 0xFF);
        previewThc = origThc;
        previewCbd = origCbd;
        previewLeafColor   = colorFromRgb(origLeafR, origLeafG, origLeafB);
        previewStrainColor = colorFromRgb(origStrainR, origStrainG, origStrainB);
        strainName  = strain.displayName() == null ? "" : strain.displayName();
        previewName = strainName;
    }

    /**
     * Called when the player clicks Save. Validates cost, deducts leaves, and immediately
     * applies the modification (the machine has no crafting-time delay).
     */
    public boolean startCrafting(String operatorName) {
        if (!itemHandler.getStackInSlot(SLOT_OUTPUT).isEmpty()) return false; // output occupied

        ItemStack seed = itemHandler.getStackInSlot(SLOT_SEED);
        if (!isModifiableSeed(seed)) return false;

        StrainData original = StrainUtil.getStrain(seed);
        if (original == StrainData.EMPTY || original.identified()) return false;

        int newLeafR   = (previewLeafColor   >> 16) & 0xFF;
        int newLeafG   = (previewLeafColor   >>  8) & 0xFF;
        int newLeafB   =  previewLeafColor          & 0xFF;
        int newStrainR = (previewStrainColor >> 16) & 0xFF;
        int newStrainG = (previewStrainColor >>  8) & 0xFF;
        int newStrainB =  previewStrainColor         & 0xFF;

        int cost = StrainModifierCostUtil.calculateCost(
                origThc, origCbd,
                origLeafR, origLeafG, origLeafB,
                origStrainR, origStrainG, origStrainB,
                previewThc, previewCbd,
                newLeafR, newLeafG, newLeafB,
                newStrainR, newStrainG, newStrainB,
                strainName, previewName
        );
        if (cost > leafMeter) return false;

        leafMeter -= cost;
        finalizeCrafting(seed, original, operatorName == null ? "" : operatorName);
        setChanged();
        return true;
    }

    private void finalizeCrafting(ItemStack seed, StrainData original, String creatorName) {
        String displayName = previewName == null ? "" : previewName.trim();
        int newThc = Math.max(0, Math.min(35, previewThc));
        int newCbd = Math.max(0, Math.min(30, previewCbd));
        int[] npk = computeNpkTarget(newThc, newCbd);

        StrainData updated = new StrainData(
                previewStrainColor,
                previewLeafColor,
                newThc,
                newCbd,
                npk[0],
                npk[1],
                npk[2],
                original.effects(),
                original.amplifier(),
                StrainEffectsUtil.computeDurationTicks(newCbd, 1.0f),
                true,
                displayName,
                original.typeColors(),
                original.baseStrain1(),
                original.baseStrain2()
        );

        // Ensure a stable lineage id so the update below can find every related item/fluid.
        String strainId = seed.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId == null || strainId.isBlank()) {
            strainId = StrainUtil.strainContentId(original);
        }

        ItemStack output = seed.copy();
        StrainUtil.setStrain(output, updated);
        output.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
        if (!creatorName.isBlank()) {
            output.set(ModDataComponentTypes.STRAIN_CREATOR.get(), creatorName);
        }

        itemHandler.setStackInSlot(SLOT_SEED, ItemStack.EMPTY);
        itemHandler.setStackInSlot(SLOT_OUTPUT, output);

        if (level instanceof ServerLevel sl) {
            StrainRegistrySavedData.get(sl.getServer()).propagateUpdate(
                    sl.getServer(), strainId, displayName, creatorName,
                    previewStrainColor, previewLeafColor, newThc, newCbd,
                    npk[0], npk[1], npk[2],
                    updated.baseStrain1(), updated.baseStrain2()
            );

            // Machine output never fires the item-entity pickup event that normally records a
            // "personal discovery" (see CommonEvents#recordStrainDiscovery), so without this the
            // player who just named/created the strain would never see it in their own Strain
            // Data Pad tab — only in the server-wide list.
            if (!creatorName.isBlank()) {
                var creator = sl.getServer().getPlayerList().getPlayerByName(creatorName);
                if (creator != null) {
                    StrainBookItem.addDiscovery(creator, strainId);
                }
            }
        }

        lastSeedStack = ItemStack.EMPTY;
    }

    /** Called server-side when the player adjusts sliders. */
    public void applySliderUpdate(int thc, int cbd,
                                   int leafR, int leafG, int leafB,
                                   int strainR, int strainG, int strainB,
                                   String name) {
        previewThc         = Math.max(0, Math.min(35, thc));
        previewCbd         = Math.max(0, Math.min(30, cbd));
        previewLeafColor   = colorFromRgb(clampColor(leafR),   clampColor(leafG),   clampColor(leafB));
        previewStrainColor = colorFromRgb(clampColor(strainR), clampColor(strainG), clampColor(strainB));
        previewName         = name == null ? "" : (name.length() > 32 ? name.substring(0, 32) : name);
        setChanged();
    }

    public void addLeafInputToMeter() {
        ItemStack leafStack = itemHandler.getStackInSlot(SLOT_LEAF);
        if (leafStack.isEmpty() || !leafStack.is(ModItems.HEMP_LEAF.get())) return;
        int toAdd = Math.min(leafStack.getCount(), MAX_LEAF_CAPACITY - leafMeter);
        if (toAdd <= 0) return;
        leafMeter += toAdd;
        leafStack.shrink(toAdd);
        if (leafStack.isEmpty()) itemHandler.setStackInSlot(SLOT_LEAF, ItemStack.EMPTY);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        addLeafInputToMeter();

        ItemStack seed = itemHandler.getStackInSlot(SLOT_SEED);
        if (!ItemStack.isSameItemSameComponents(seed, lastSeedStack)) {
            recalcPreview();
            lastSeedStack = seed.copy();
        }

        leafMeter = Math.max(0, Math.min(MAX_LEAF_CAPACITY, leafMeter));

        boolean shouldBePowered = hasSeed();
        if (state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED) != shouldBePowered) {
            level.setBlockAndUpdate(pos, state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED, shouldBePowered));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Derives a strain's target N/P/K (the fertilizer levels a grower must reach for full
     * potency/yield, see {@code BaseWeedCropBlockEntity}/{@code GrowPotBlockEntity}) from its
     * THC/CBD — higher THC makes the target harder to hit (bigger numbers, more fertilizer
     * applications needed since custom strains start every nutrient at 0), higher CBD makes it
     * easier. THC (0-35) pulls difficulty up, CBD (0-30) pulls it back down; the result is
     * clamped to a 0-1 "difficulty" scalar and mapped onto a 5-22 range per nutrient, matching
     * the existing hand-authored strain presets (StrainRegistry's easiest strains sit around
     * 6-11 per nutrient, its hardest around 15-22).
     */
    private static int[] computeNpkTarget(int thc, int cbd) {
        double thcFactor = Math.max(0, Math.min(35, thc)) / 35.0;
        double cbdFactor = Math.max(0, Math.min(30, cbd)) / 30.0;
        double difficulty = Math.max(0.0, Math.min(1.0, thcFactor - 0.4 * cbdFactor));

        int baseline = 5;
        int range = 15;
        int n = baseline + (int) Math.round(range * difficulty * 0.9);
        int p = baseline + (int) Math.round(range * difficulty * 1.0);
        int k = baseline + (int) Math.round(range * difficulty * 1.1);
        return new int[]{n, p, k};
    }

    private static int clampColor(int v) { return Math.max(COLOR_MIN, Math.min(COLOR_MAX, v)); }
    private static int colorFromRgb(int r, int g, int b) {
        return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static boolean isModifiableSeed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof UnidentifiedSeedsItem) {
            StrainData d = StrainUtil.getStrain(stack);
            return d != StrainData.EMPTY && !d.identified();
        }
        StrainData d = StrainUtil.getStrain(stack);
        return d != StrainData.EMPTY && !d.identified() && stack.has(ModDataComponentTypes.STRAIN_DATA.get());
    }

    @Override public Component getDisplayName() { return Component.translatable("block.smokeleafindustries.strain_modifier"); }
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new StrainModifierMenu(containerId, inventory, this, this.data);
    }
    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) inv.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    // NBT Data
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("strain_modifier.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("strain_modifier.leafMeter", leafMeter);
        tag.putInt("strain_modifier.energy", energyStorage.getEnergyStored());

        tag.putInt("strain_modifier.previewThc", previewThc);
        tag.putInt("strain_modifier.previewCbd", previewCbd);
        tag.putInt("strain_modifier.previewLeafColor", previewLeafColor);
        tag.putInt("strain_modifier.previewStrainColor", previewStrainColor);

        tag.putInt("strain_modifier.origThc", origThc);
        tag.putInt("strain_modifier.origCbd", origCbd);
        tag.putInt("strain_modifier.origLeafR", origLeafR);
        tag.putInt("strain_modifier.origLeafG", origLeafG);
        tag.putInt("strain_modifier.origLeafB", origLeafB);
        tag.putInt("strain_modifier.origStrainR", origStrainR);
        tag.putInt("strain_modifier.origStrainG", origStrainG);
        tag.putInt("strain_modifier.origStrainB", origStrainB);

        tag.putString("strain_modifier.strainName", strainName);
        tag.putString("strain_modifier.previewName", previewName);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("strain_modifier.inventory"));
        energyStorage.setEnergy(tag.getInt("strain_modifier.energy"));
        leafMeter   = tag.getInt("strain_modifier.leafMeter");

        previewThc         = tag.getInt("strain_modifier.previewThc");
        previewCbd          = tag.getInt("strain_modifier.previewCbd");
        previewLeafColor    = tag.contains("strain_modifier.previewLeafColor") ? tag.getInt("strain_modifier.previewLeafColor") : previewLeafColor;
        previewStrainColor  = tag.contains("strain_modifier.previewStrainColor") ? tag.getInt("strain_modifier.previewStrainColor") : previewStrainColor;

        origThc    = tag.getInt("strain_modifier.origThc");
        origCbd    = tag.getInt("strain_modifier.origCbd");
        origLeafR  = tag.contains("strain_modifier.origLeafR") ? tag.getInt("strain_modifier.origLeafR") : origLeafR;
        origLeafG  = tag.contains("strain_modifier.origLeafG") ? tag.getInt("strain_modifier.origLeafG") : origLeafG;
        origLeafB  = tag.contains("strain_modifier.origLeafB") ? tag.getInt("strain_modifier.origLeafB") : origLeafB;
        origStrainR = tag.contains("strain_modifier.origStrainR") ? tag.getInt("strain_modifier.origStrainR") : origStrainR;
        origStrainG = tag.contains("strain_modifier.origStrainG") ? tag.getInt("strain_modifier.origStrainG") : origStrainG;
        origStrainB = tag.contains("strain_modifier.origStrainB") ? tag.getInt("strain_modifier.origStrainB") : origStrainB;

        strainName  = tag.getString("strain_modifier.strainName");
        previewName = tag.getString("strain_modifier.previewName");
    }

    // Server / Client Syncing
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}
