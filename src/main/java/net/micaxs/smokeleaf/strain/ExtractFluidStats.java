package net.micaxs.smokeleaf.strain;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.micaxs.smokeleaf.Config;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves per-extract stats (N/P/K and optionally THC/CBD) for extract fluids.
 *
 * Order of precedence:
 * 1) User config overrides (if present)
 * 2) Hardcoded defaults (so the game works out of the box)
 */
public final class ExtractFluidStats {
    private ExtractFluidStats() {}

    public record Stats(int n, int p, int k, int thc, int cbd) {
        public static final Stats EMPTY = new Stats(0, 0, 0, 0, 0);

        public boolean hasNpk() {
            return n != 0 || p != 0 || k != 0;
        }

        public boolean hasCannabinoids() {
            return thc != 0 || cbd != 0;
        }
    }

    // ---- Defaults (code) ----
    // Keep this minimal and easy to extend. Users can override via config.
    private static final Map<ResourceLocation, Stats> DEFAULTS = new HashMap<>();

    static {
        // These mimic the crop nutrient targets (feel free to tune).
        // Fluids are "smokeleafindustries:<name>_extract_fluid".
        putDefault("white_widow_extract_fluid", 11, 8, 10);
        putDefault("purple_haze_extract_fluid", 11, 13, 7);
        putDefault("bubble_kush_extract_fluid", 12, 10, 18);
        putDefault("lemon_haze_extract_fluid", 9, 12, 15);
        putDefault("sour_diesel_extract_fluid", 11, 13, 10);
        putDefault("blue_ice_extract_fluid", 14, 9, 14);
        putDefault("bubblegum_extract_fluid", 14, 14, 12);
        putDefault("og_kush_extract_fluid", 12, 13, 15);
        putDefault("jack_herer_extract_fluid", 10, 15, 10);
        putDefault("gary_peyton_extract_fluid", 14, 15, 9);
        putDefault("amnesia_haze_extract_fluid", 10, 13, 9);
        putDefault("ak47_extract_fluid", 17, 8, 11);
        putDefault("ghost_train_extract_fluid", 13, 12, 10);
        putDefault("grape_ape_extract_fluid", 15, 13, 15);
        putDefault("cotton_candy_extract_fluid", 10, 13, 9);
        putDefault("banana_kush_extract_fluid", 15, 13, 15);
        putDefault("carbon_fiber_extract_fluid", 14, 13, 20);
        putDefault("birthday_cake_extract_fluid", 11, 13, 16);
        putDefault("blue_cookies_extract_fluid", 10, 13, 9);
        putDefault("afghani_extract_fluid", 11, 10, 19);
        putDefault("moonbow_extract_fluid", 15, 2, 22);
        putDefault("lava_cake_extract_fluid", 14, 12, 11);
        putDefault("jelly_rancher_extract_fluid", 11, 14, 9);
        putDefault("strawberry_shortcake_extract_fluid", 14, 11, 15);
        putDefault("pink_kush_extract_fluid", 17, 9, 12);
    }

    private static void putDefault(String path, int n, int p, int k) {
        DEFAULTS.put(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, path), new Stats(n, p, k, 0, 0));
    }

    public static Stats get(Fluid fluid) {
        if (fluid == null) return Stats.EMPTY;
        ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
        if (id == null) return Stats.EMPTY;

        // 1) Config overrides: map fluid path -> stats.
        Optional<Stats> fromCfg = lookupFromConfig(id);
        if (fromCfg.isPresent()) return fromCfg.get();

        // 2) Defaults
        Stats def = DEFAULTS.get(id);
        return def != null ? def : Stats.EMPTY;
    }

    private static Optional<Stats> lookupFromConfig(ResourceLocation fluidId) {
        // Reuse existing config structure pattern: a list of single-entry tables.
        // We'll extend Config with FLUID_EXTRACT_STATS; if it's not present (older config), return empty.
        List<? extends UnmodifiableConfig> list;
        try {
            list = Config.EXTRACT_FLUID_STATS.get();
        } catch (Throwable ignored) {
            return Optional.empty();
        }
        if (list == null) return Optional.empty();

        for (UnmodifiableConfig outer : list) {
            if (outer == null) continue;
            Map<String, Object> outerMap = outer.valueMap();
            if (outerMap.isEmpty()) continue;

            var e = outerMap.entrySet().iterator().next();
            String key = e.getKey();
            Object innerObj = e.getValue();
            if (!(innerObj instanceof UnmodifiableConfig inner)) continue;

            // match either full id or just path
            boolean match = key.equals(fluidId.toString()) || key.equals(fluidId.getPath());
            if (!match) continue;

            int n = toInt(inner.get("n"), 0);
            int p = toInt(inner.get("p"), 0);
            int k = toInt(inner.get("k"), 0);
            int thc = toInt(inner.get("thc"), 0);
            int cbd = toInt(inner.get("cbd"), 0);
            return Optional.of(new Stats(n, p, k, thc, cbd));
        }

        return Optional.empty();
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number num) return num.intValue();
        if (o instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return def;
    }
}
