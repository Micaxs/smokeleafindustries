package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Server-wide persistent registry that maps a {@code strainId} (UUID string or mix key)
 * to a {@link StrainEntry} containing the display name and the player who named it.
 *
 * <p>Items (seeds, buds, weeds, extracts, oil fluids/buckets) carry a {@code STRAIN_ID}
 * data component that references an entry here. When a strain is renamed, this registry
 * is updated and the new name is propagated to all currently-loaded items in online
 * players' inventories.
 */
public class StrainRegistrySavedData extends SavedData {

    private static final String SAVE_KEY = "smokeleaf_strain_registry";

    private final Map<String, StrainEntry> entries = new HashMap<>();

    // -----------------------------------------------------------------------
    // Entry record
    // -----------------------------------------------------------------------

    public record StrainEntry(String strainId, String displayName, String creatorName) {}

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
    public void register(String strainId, String displayName, String creatorName) {
        entries.put(strainId, new StrainEntry(strainId, displayName, creatorName));
        setDirty();
    }

    /**
     * Renames an existing entry and propagates the new name to every item with this
     * {@code strainId} that is currently in an online player's inventory / cursor.
     */
    public void propagateRename(MinecraftServer server, String strainId, String newName, String creatorName) {
        StrainEntry existing = entries.get(strainId);
        String creator = (existing != null && !existing.creatorName().isBlank())
                ? existing.creatorName() : creatorName;
        entries.put(strainId, new StrainEntry(strainId, newName, creator));
        setDirty();

        // Update all matching items in online players' inventories
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updateInventory(player, strainId, newName);
        }
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

    // -----------------------------------------------------------------------
    // Sync helpers
    // -----------------------------------------------------------------------

    /**
     * Syncs the embedded {@link StrainData#displayName()} of every item in the player's
     * inventory / hotbar / offhand / armor to match the registry. Call on login.
     */
    public void syncPlayerInventory(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) syncItem(stack);
        for (ItemStack stack : player.getInventory().armor) syncItem(stack);
        for (ItemStack stack : player.getInventory().offhand) syncItem(stack);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void updateInventory(ServerPlayer player, String strainId, String newName) {
        for (ItemStack stack : player.getInventory().items) applyRename(stack, strainId, newName);
        for (ItemStack stack : player.getInventory().armor) applyRename(stack, strainId, newName);
        for (ItemStack stack : player.getInventory().offhand) applyRename(stack, strainId, newName);
        applyRename(player.containerMenu.getCarried(), strainId, newName);
    }

    private static void applyRename(ItemStack stack, String strainId, String newName) {
        if (stack.isEmpty()) return;
        String itemStrainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (!strainId.equals(itemStrainId)) return;
        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;
        StrainUtil.setStrain(stack, renamed(d, newName));
    }

    private void syncItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        String strainId = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (strainId == null) return;
        String registryName = lookupName(strainId);
        if (registryName == null || registryName.isBlank()) return;
        StrainData d = StrainUtil.getStrain(stack);
        if (d == StrainData.EMPTY) return;
        if (!registryName.equals(d.displayName())) {
            StrainUtil.setStrain(stack, renamed(d, registryName));
        }
    }

    private static StrainData renamed(StrainData d, String newName) {
        return new StrainData(
                d.colorArgb(), d.leafColor(), d.thc(), d.cbd(),
                d.nitrogen(), d.phosphorus(), d.potassium(),
                d.effects(), d.amplifier(), d.durationTicks(),
                true, newName, d.typeColors()
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
                data.entries.put(key, new StrainEntry(key, displayName, creatorName));
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
            entriesTag.put(e.getKey(), entry);
        }
        tag.put("entries", entriesTag);
        return tag;
    }
}
