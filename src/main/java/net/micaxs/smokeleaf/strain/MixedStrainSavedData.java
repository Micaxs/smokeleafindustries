package net.micaxs.smokeleaf.strain;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

/**
 * Server-side persistent store mapping canonical mix keys → custom strain names.
 *
 * A "mix key" is a canonical string built from the two parent strain display names,
 * sorted alphabetically and joined with "||". This makes A+B and B+A identical.
 *
 * Stored in world/data/smokeleaf_mixed_strains.dat — survives server restarts.
 */
public class MixedStrainSavedData extends SavedData {

    public static final String DATA_NAME = "smokeleaf_mixed_strains";

    private final Map<String, String> mixToName = new HashMap<>();

    // -----------------------------------------------------------------------
    // Access
    // -----------------------------------------------------------------------

    public static MixedStrainSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MixedStrainSavedData::new, MixedStrainSavedData::load),
                DATA_NAME
        );
    }

    // -----------------------------------------------------------------------
    // Key helpers
    // -----------------------------------------------------------------------

    /** Produces a canonical (order-independent) key from two parent strain names. */
    public static String canonicalKey(String nameA, String nameB) {
        if (nameA == null) nameA = "";
        if (nameB == null) nameB = "";
        if (nameA.compareTo(nameB) <= 0) return nameA + "||" + nameB;
        return nameB + "||" + nameA;
    }

    // -----------------------------------------------------------------------
    // Mutations
    // -----------------------------------------------------------------------

    /** Register a custom name for the combination identified by {@code mixKey}. */
    public void register(String mixKey, String customName) {
        if (mixKey == null || mixKey.isBlank() || customName == null || customName.isBlank()) return;
        mixToName.put(mixKey, customName);
        setDirty();
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    /** Returns the registered custom name for {@code mixKey}, or {@code null} if not yet named. */
    public String lookup(String mixKey) {
        if (mixKey == null) return null;
        return mixToName.get(mixKey);
    }

    // -----------------------------------------------------------------------
    // Serialization
    // -----------------------------------------------------------------------

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider regs) {
        CompoundTag map = new CompoundTag();
        mixToName.forEach(map::putString);
        tag.put("mixes", map);
        return tag;
    }

    public static MixedStrainSavedData load(CompoundTag tag, HolderLookup.Provider regs) {
        MixedStrainSavedData data = new MixedStrainSavedData();
        CompoundTag map = tag.getCompound("mixes");
        for (String key : map.getAllKeys()) {
            data.mixToName.put(key, map.getString(key));
        }
        return data;
    }
}
