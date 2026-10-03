package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Server-wide persistent registry that maps a {@code strainId} (UUID string or mix key)
 * to a {@link StrainEntry} containing the strain's current name, creator, and appearance/stats.
 *
 * <p>Items (seeds, buds, weeds, extracts, oil fluids/buckets) carry a {@code STRAIN_ID}
 * data component that references an entry here. When a strain is edited (renamed, recolored,
 * or its THC/CBD/N-P-K adjusted, e.g. via the Strain Identifier), this registry is updated and
 * the change is propagated to all currently-loaded items in online players' inventories and to
 * matching fluid tanks in loaded machines (see {@link StrainTankTracker}).
 */
public class StrainRegistrySavedData extends SavedData {

    private static final String SAVE_KEY = "smokeleaf_strain_registry";

    private final Map<String, StrainEntry> entries = new HashMap<>();

    // -----------------------------------------------------------------------
    // Entry record
    // -----------------------------------------------------------------------

    /**
     * @param colorArgb bud/strain colour, or {@code null} if this strain predates appearance tracking
     * @param leafColor leaf colour, or {@code null} if this strain predates appearance tracking
     * @param thc       THC value, or {@code null} if this strain predates appearance tracking
     * @param cbd       CBD value, or {@code null} if this strain predates appearance tracking
     * @param nitrogen  target nitrogen, or {@code null} if this strain predates NPK tracking
     * @param phosphorus target phosphorus, or {@code null} if this strain predates NPK tracking
     * @param potassium target potassium, or {@code null} if this strain predates NPK tracking
     * @param baseStrain1 first parent strain's name if this strain was crossed, else blank
     * @param baseStrain2 second parent strain's name if this strain was crossed, else blank
     */
    public record StrainEntry(String strainId, String displayName, String creatorName,
                               Integer colorArgb, Integer leafColor, Integer thc, Integer cbd,
                               Integer nitrogen, Integer phosphorus, Integer potassium,
                               String baseStrain1, String baseStrain2) {}

    // -----------------------------------------------------------------------
    // Factory / access
    // -----------------------------------------------------------------------

    public static StrainRegistrySavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(new Factory<>(StrainRegistrySavedData::new, StrainRegistrySavedData::load), SAVE_KEY);
    }

    // -----------------------------------------------------------------------
    // Mutations
    // -----------------------------------------------------------------------

    /** Registers (or overwrites) an entry for {@code strainId}. */
    public void register(String strainId, String displayName, String creatorName,
                          Integer colorArgb, Integer leafColor, Integer thc, Integer cbd,
                          Integer nitrogen, Integer phosphorus, Integer potassium,
                          String baseStrain1, String baseStrain2) {
        entries.put(strainId, new StrainEntry(strainId, displayName, creatorName, colorArgb, leafColor, thc, cbd,
                nitrogen, phosphorus, potassium,
                baseStrain1 == null ? "" : baseStrain1, baseStrain2 == null ? "" : baseStrain2));
        setDirty();
    }

    /**
     * Applies a full update (name, creator, colour, THC/CBD, N/P/K) to an existing entry and
     * propagates it to every item with this {@code strainId} that is currently in an online
     * player's inventory / cursor, as well as any loaded machine tank holding matching fluid.
     */
    public void propagateUpdate(MinecraftServer server, String strainId, String newName, String newCreator,
                                 int colorArgb, int leafColor, int thc, int cbd,
                                 int nitrogen, int phosphorus, int potassium,
                                 String baseStrain1, String baseStrain2) {
        StrainEntry entry = new StrainEntry(strainId, newName, newCreator, colorArgb, leafColor, thc, cbd,
                nitrogen, phosphorus, potassium,
                baseStrain1 == null ? "" : baseStrain1, baseStrain2 == null ? "" : baseStrain2);
        entries.put(strainId, entry);
        setDirty();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updateInventory(player, strainId, entry);
        }

        StrainTankTracker.propagate(strainId, entry);
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    @Nullable
    public String lookupName(String strainId) {
        StrainEntry e = entries.get(strainId);
        return e != null ? e.displayName() : null;
    }

    @Nullable
    public StrainEntry lookup(String strainId) {
        return entries.get(strainId);
    }

    /** Every strain any player has registered/named server-wide (used by the Strain Data Pad's Server tab). */
    public Map<String, StrainEntry> allEntries() {
        return Map.copyOf(entries);
    }

    // -----------------------------------------------------------------------
    // Sync helpers
    // -----------------------------------------------------------------------

    /**
     * Syncs the embedded {@link StrainData} of every item in the player's inventory / hotbar /
     * offhand / armor to match the registry. Call on login.
     */
    public void syncPlayerInventory(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) syncItem(stack);
        for (ItemStack stack : player.getInventory().armor) syncItem(stack);
        for (ItemStack stack : player.getInventory().offhand) syncItem(stack);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void updateInventory(ServerPlayer player, String strainId, StrainEntry entry) {
        for (ItemStack stack : player.getInventory().items) applyUpdate(stack, strainId, entry);
        for (ItemStack stack : player.getInventory().armor) applyUpdate(stack, strainId, entry);
        for (ItemStack stack : player.getInventory().offhand) applyUpdate(stack, strainId, entry);
        applyUpdate(player.containerMenu.getCarried(), strainId, entry);

        // Patching every matching stack in place (above) makes them component-identical, but
        // vanilla never retroactively re-merges stacks that become equal after an in-place NBT/
        // component mutation — only explicit pickup/transfer does that. Without this, a freshly
        // identified seed sitting in one slot and older already-updated copies elsewhere just stay
        // as separate, needlessly-split stacks instead of combining.
        consolidateStacks(player.getInventory().items);
    }

    /** Merges any now-identical adjacent stacks in {@code slots} (see {@link #updateInventory}). */
    private static void consolidateStacks(net.minecraft.core.NonNullList<ItemStack> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack target = slots.get(i);
            if (target.isEmpty() || target.getCount() >= target.getMaxStackSize()) continue;
            for (int j = i + 1; j < slots.size(); j++) {
                ItemStack source = slots.get(j);
                if (source.isEmpty() || !ItemStack.isSameItemSameComponents(target, source)) continue;
                int space = target.getMaxStackSize() - target.getCount();
                if (space <= 0) break;
                int moved = Math.min(space, source.getCount());
                target.grow(moved);
                source.shrink(moved);
                if (source.isEmpty()) slots.set(j, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Patches {@code stack} in place if it carries the given {@code strainId} — reused both for
     * items in online players' inventories (see {@link #updateInventory}) and by block entities
     * that hold matching items in their own slots (e.g. a Mutator's output slot; see
     * {@link StrainTankHolder#applyStrainRegistryUpdate}, whose name predates item-slot support
     * but whose registration/dispatch machinery is generic enough to cover both).
     */
    public static void applyUpdate(ItemStack stack, String strainId, StrainEntry entry) {
        if (stack.isEmpty()) return;
        String itemStrainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (!strainId.equals(itemStrainId)) return;
        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;
        StrainUtil.setStrain(stack, withEntryApplied(d, entry));
        if (!entry.creatorName().isBlank()) stack.set(ModDataComponentTypes.STRAIN_CREATOR.get(), entry.creatorName());
        stack.remove(DataComponents.CUSTOM_NAME);
    }

    private void syncItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        String strainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId == null) return;
        StrainEntry entry = lookup(strainId);
        if (entry == null || entry.displayName().isBlank()) return;
        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;
        if (!entriesMatch(d, entry)) {
            StrainUtil.setStrain(stack, withEntryApplied(d, entry));
        }
        if (!entry.creatorName().isBlank()) {
            stack.set(ModDataComponentTypes.STRAIN_CREATOR.get(), entry.creatorName());
        }
        stack.remove(DataComponents.CUSTOM_NAME);
    }

    private static boolean entriesMatch(StrainData d, StrainEntry entry) {
        int finalCbd = entry.cbd() != null ? entry.cbd() : d.cbd();
        return entry.displayName().equals(d.displayName())
                && (entry.colorArgb() == null || entry.colorArgb() == d.colorArgb())
                && (entry.leafColor() == null || entry.leafColor() == d.leafColor())
                && (entry.thc() == null || entry.thc() == d.thc())
                && (entry.cbd() == null || entry.cbd() == d.cbd())
                && (entry.nitrogen() == null || entry.nitrogen() == d.nitrogen())
                && (entry.phosphorus() == null || entry.phosphorus() == d.phosphorus())
                && (entry.potassium() == null || entry.potassium() == d.potassium())
                && (entry.baseStrain1().isBlank() || entry.baseStrain1().equals(d.baseStrain1()))
                && (entry.baseStrain2().isBlank() || entry.baseStrain2().equals(d.baseStrain2()))
                // durationTicks is derived from CBD (see withEntryApplied) — stacks created before
                // that normalization existed can still be carrying a stale/mismatched value, which
                // alone is enough to block two otherwise-identical stacks from merging, so this has
                // to be checked here too or syncItem would treat them as "already matching" and
                // never actually fix them.
                && d.durationTicks() == StrainEffectsUtil.computeDurationTicks(finalCbd, 1.0f);
    }

    /**
     * Rebuilds {@code d} with the entry's tracked fields applied — colour/THC/CBD/NPK/parentage are
     * only overridden when the entry actually carries a value (older, pre-tracking entries leave
     * these {@code null}/blank so they don't clobber an item's existing stats).
     */
    public static StrainData withEntryApplied(StrainData d, StrainEntry entry) {
        int finalCbd = entry.cbd() != null ? entry.cbd() : d.cbd();
        return new StrainData(
                entry.colorArgb() != null ? entry.colorArgb() : d.colorArgb(),
                entry.leafColor() != null ? entry.leafColor() : d.leafColor(),
                entry.thc() != null ? entry.thc() : d.thc(),
                finalCbd,
                entry.nitrogen() != null ? entry.nitrogen() : d.nitrogen(),
                entry.phosphorus() != null ? entry.phosphorus() : d.phosphorus(),
                entry.potassium() != null ? entry.potassium() : d.potassium(),
                // durationTicks is always re-derived from the final CBD (see StrainEffectsUtil)
                // rather than kept as whatever this particular stack happened to have — two stacks
                // of the same identified strain must converge on the exact same duration, or that
                // one leftover field keeps them from ever being allowed to stack together.
                d.effects(), d.amplifier(), StrainEffectsUtil.computeDurationTicks(finalCbd, 1.0f),
                true, entry.displayName(), d.typeColors(),
                !entry.baseStrain1().isBlank() ? entry.baseStrain1() : d.baseStrain1(),
                !entry.baseStrain2().isBlank() ? entry.baseStrain2() : d.baseStrain2()
        );
    }

    // -----------------------------------------------------------------------
    // Serialization
    // -----------------------------------------------------------------------

    public static StrainRegistrySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        StrainRegistrySavedData data = new StrainRegistrySavedData();
        if (tag.contains("entries")) {
            CompoundTag entriesTag = tag.getCompound("entries");
            for (String key : entriesTag.getAllKeys()) {
                CompoundTag entry = entriesTag.getCompound(key);
                String displayName = entry.getString("name");
                String creatorName = entry.getString("creator");
                Integer colorArgb = entry.contains("colorArgb") ? entry.getInt("colorArgb") : null;
                Integer leafColor = entry.contains("leafColor") ? entry.getInt("leafColor") : null;
                Integer thc = entry.contains("thc") ? entry.getInt("thc") : null;
                Integer cbd = entry.contains("cbd") ? entry.getInt("cbd") : null;
                Integer nitrogen = entry.contains("nitrogen") ? entry.getInt("nitrogen") : null;
                Integer phosphorus = entry.contains("phosphorus") ? entry.getInt("phosphorus") : null;
                Integer potassium = entry.contains("potassium") ? entry.getInt("potassium") : null;
                String baseStrain1 = entry.getString("baseStrain1");
                String baseStrain2 = entry.getString("baseStrain2");
                data.entries.put(key, new StrainEntry(key, displayName, creatorName, colorArgb, leafColor, thc, cbd,
                        nitrogen, phosphorus, potassium, baseStrain1, baseStrain2));
            }
        }
        return data;
    }

    // Overload without registries (called by Factory::new for a blank instance)
    public static StrainRegistrySavedData load(CompoundTag tag) {
        return load(tag, null);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag entriesTag = new CompoundTag();
        for (Map.Entry<String, StrainEntry> e : entries.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("name", e.getValue().displayName());
            entry.putString("creator", e.getValue().creatorName());
            if (e.getValue().colorArgb() != null) entry.putInt("colorArgb", e.getValue().colorArgb());
            if (e.getValue().leafColor() != null) entry.putInt("leafColor", e.getValue().leafColor());
            if (e.getValue().thc() != null) entry.putInt("thc", e.getValue().thc());
            if (e.getValue().cbd() != null) entry.putInt("cbd", e.getValue().cbd());
            if (e.getValue().nitrogen() != null) entry.putInt("nitrogen", e.getValue().nitrogen());
            if (e.getValue().phosphorus() != null) entry.putInt("phosphorus", e.getValue().phosphorus());
            if (e.getValue().potassium() != null) entry.putInt("potassium", e.getValue().potassium());
            if (!e.getValue().baseStrain1().isBlank()) entry.putString("baseStrain1", e.getValue().baseStrain1());
            if (!e.getValue().baseStrain2().isBlank()) entry.putString("baseStrain2", e.getValue().baseStrain2());
            entriesTag.put(e.getKey(), entry);
        }
        tag.put("entries", entriesTag);
        return tag;
    }
}
