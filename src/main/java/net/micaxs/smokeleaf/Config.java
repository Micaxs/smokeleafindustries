package net.micaxs.smokeleaf;

import java.util.*;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> PLANT_NUTRIENTS =
            BUILDER.defineListAllowEmpty(
                            "plant_nutrients",
                            defaultPlantNutrients(),
                            Config::validatePlantNutrientsElement
                    );

    /**
     * Per-extract-fluid stat overrides.
     *
     * Format (list entries):
     *   { "white_widow_extract_fluid": { n=6, p=1, k=9, thc=0, cbd=0 } }
     * Keys can be either full resource locations ("smokeleafindustries:white_widow_extract_fluid")
     * or just the path ("white_widow_extract_fluid").
     */
    public static final ModConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> EXTRACT_FLUID_STATS =
            BUILDER.defineListAllowEmpty(
                    "extract_fluid_stats",
                    defaultExtractFluidStats(),
                    Config::validateExtractFluidStatsElement
            );

    static final ModConfigSpec SPEC = BUILDER.build();

    // Cache resolved targets for quick lookup; keys include both "namespace:path" and plain "path".
    private static final Map<String, NutrientTarget> PLANT_NUTRIENTS_CACHE = new HashMap<>();

    public static final class NutrientTarget {
        public final int n;
        public final int p;
        public final int k;

        public NutrientTarget(int n, int p, int k) {
            this.n = n;
            this.p = p;
            this.k = k;
        }
    }

    // Defaults using NightConfig in-memory tables so the writer can serialize them.
    private static List<UnmodifiableConfig> defaultPlantNutrients() {
        List<UnmodifiableConfig> list = new ArrayList<>();

        // 🟢 Easy (1-2 fertilizers) - 5 crops
        list.add(make("white_widow_crop", 11, 8, 10));
        list.add(make("bubble_kush_crop", 12, 10, 18));
        list.add(make("amnesia_haze_crop", 10, 13, 9));
        list.add(make("blue_cookies_crop", 10, 13, 9));
        list.add(make("ghost_train_crop", 13, 12, 10));

        // 🟡 Medium (2-3 fertilizers) - 10 crops
        list.add(make("sour_diesel_crop", 11, 13, 10));
        list.add(make("lemon_haze_crop", 9, 12, 15));
        list.add(make("jack_herer_crop", 10, 15, 10));
        list.add(make("grape_ape_crop", 15, 13, 15));
        list.add(make("cotton_candy_crop", 10, 13, 9));
        list.add(make("afghani_crop", 11, 10, 19));
        list.add(make("lava_cake_crop", 14, 12, 11));
        list.add(make("jelly_rancher_crop", 11, 14, 9));
        list.add(make("strawberry_shortcake_crop", 14, 11, 15));
        list.add(make("purple_haze_crop", 11, 13, 7));

        // 🟠 Hard (3-4 fertilizers) - 6 crops
        list.add(make("blue_ice_crop", 14, 9, 14));
        list.add(make("bubblegum_crop", 14, 14, 12));
        list.add(make("gary_peyton_crop", 14, 15, 9));
        list.add(make("ak47_crop", 17, 8, 11));
        list.add(make("banana_kush_crop", 15, 13, 15));
        list.add(make("pink_kush_crop", 17, 9, 12));

        // 🔴 Expert (5 fertilizers) - 4 crops
        list.add(make("og_kush_crop", 12, 13, 15));
        list.add(make("carbon_fiber_crop", 14, 13, 20));
        list.add(make("birthday_cake_crop", 11, 13, 16));
        list.add(make("moonbow_crop", 15, 2, 22));

        return List.copyOf(list);
    }

    private static UnmodifiableConfig make(String cropName, int n, int p, int k) {
        var inner = com.electronwill.nightconfig.core.Config.inMemory();
        inner.set("n", n);
        inner.set("p", p);
        inner.set("k", k);
        var outer = com.electronwill.nightconfig.core.Config.inMemory();
        outer.set(cropName, inner);
        return outer.unmodifiable();
    }

    private static boolean validatePlantNutrientsElement(Object o) {
        if (!(o instanceof UnmodifiableConfig outer)) return false;
        Map<String, Object> outerMap = outer.valueMap();
        if (outerMap.isEmpty() || outerMap.size() != 1) return false;

        var entry = outerMap.entrySet().iterator().next();
        Object innerObj = entry.getValue();
        if (!(innerObj instanceof UnmodifiableConfig inner)) return false;

        Object n = inner.get("n");
        Object p = inner.get("p");
        Object k = inner.get("k");
        return isIntLike(n) && isIntLike(p) && isIntLike(k);
    }

    private static boolean isIntLike(Object o) {
        if (o instanceof Number) return true;
        if (o instanceof String s) {
            try { Integer.parseInt(s); return true; } catch (NumberFormatException ignored) {}
        }
        return false;
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number num) return num.intValue();
        if (o instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

    private static void rebuildPlantNutrientsCache() {
        PLANT_NUTRIENTS_CACHE.clear();
        List<? extends UnmodifiableConfig> list = PLANT_NUTRIENTS.get();
        if (list == null) return;

        for (UnmodifiableConfig outer : list) {
            if (outer == null) continue;
            Map<String, Object> outerMap = outer.valueMap();
            if (outerMap.isEmpty()) continue;

            var e = outerMap.entrySet().iterator().next();
            String key = e.getKey(); // may be "namespace:path" or just "path"
            Object innerObj = e.getValue();
            if (!(innerObj instanceof UnmodifiableConfig inner)) continue;

            int n = toInt(inner.get("n"), 0);
            int p = toInt(inner.get("p"), 0);
            int k = toInt(inner.get("k"), 0);
            NutrientTarget target = new NutrientTarget(n, p, k);

            // Keep exactly as provided
            PLANT_NUTRIENTS_CACHE.put(key, target);

            // If the key is a full RL, also index by its path for convenience
            try {
                ResourceLocation rl = ResourceLocation.parse(key);
                PLANT_NUTRIENTS_CACHE.putIfAbsent(rl.getPath(), target);
            } catch (Exception ignored) {
                // If the key is just a path, that's fine.
            }
        }
    }

    public static Optional<NutrientTarget> getNutrientTargetFor(ResourceLocation id) {
        if (id == null) return Optional.empty();
        NutrientTarget t = PLANT_NUTRIENTS_CACHE.get(id.toString());
        if (t != null) return Optional.of(t);
        t = PLANT_NUTRIENTS_CACHE.get(id.getPath());
        return Optional.ofNullable(t);
    }

    private static List<UnmodifiableConfig> defaultExtractFluidStats() {
        List<UnmodifiableConfig> list = new ArrayList<>();
        // Defaults mirror plant nutrient targets; users can override per extract fluid.
        list.add(makeExtract("white_widow_extract_fluid", 11, 8, 10, 0, 0));
        list.add(makeExtract("bubble_kush_extract_fluid", 12, 10, 18, 0, 0));
        list.add(makeExtract("amnesia_haze_extract_fluid", 10, 13, 9, 0, 0));
        list.add(makeExtract("blue_cookies_extract_fluid", 10, 13, 9, 0, 0));
        list.add(makeExtract("ghost_train_extract_fluid", 13, 12, 10, 0, 0));

        list.add(makeExtract("sour_diesel_extract_fluid", 11, 13, 10, 0, 0));
        list.add(makeExtract("lemon_haze_extract_fluid", 9, 12, 15, 0, 0));
        list.add(makeExtract("jack_herer_extract_fluid", 10, 15, 10, 0, 0));
        list.add(makeExtract("grape_ape_extract_fluid", 15, 13, 15, 0, 0));
        list.add(makeExtract("cotton_candy_extract_fluid", 10, 13, 9, 0, 0));
        list.add(makeExtract("afghani_extract_fluid", 11, 10, 19, 0, 0));
        list.add(makeExtract("lava_cake_extract_fluid", 14, 12, 11, 0, 0));
        list.add(makeExtract("jelly_rancher_extract_fluid", 11, 14, 9, 0, 0));
        list.add(makeExtract("strawberry_shortcake_extract_fluid", 14, 11, 15, 0, 0));
        list.add(makeExtract("purple_haze_extract_fluid", 11, 13, 7, 0, 0));

        list.add(makeExtract("blue_ice_extract_fluid", 14, 9, 14, 0, 0));
        list.add(makeExtract("bubblegum_extract_fluid", 14, 14, 12, 0, 0));
        list.add(makeExtract("gary_peyton_extract_fluid", 14, 15, 9, 0, 0));
        list.add(makeExtract("ak47_extract_fluid", 17, 8, 11, 0, 0));
        list.add(makeExtract("banana_kush_extract_fluid", 15, 13, 15, 0, 0));
        list.add(makeExtract("pink_kush_extract_fluid", 17, 9, 12, 0, 0));

        list.add(makeExtract("og_kush_extract_fluid", 12, 13, 15, 0, 0));
        list.add(makeExtract("carbon_fiber_extract_fluid", 14, 13, 20, 0, 0));
        list.add(makeExtract("birthday_cake_extract_fluid", 11, 13, 16, 0, 0));
        list.add(makeExtract("moonbow_extract_fluid", 15, 2, 22, 0, 0));

        return List.copyOf(list);
    }

    private static UnmodifiableConfig makeExtract(String fluidName, int n, int p, int k, int thc, int cbd) {
        var inner = com.electronwill.nightconfig.core.Config.inMemory();
        inner.set("n", n);
        inner.set("p", p);
        inner.set("k", k);
        inner.set("thc", thc);
        inner.set("cbd", cbd);
        var outer = com.electronwill.nightconfig.core.Config.inMemory();
        outer.set(fluidName, inner);
        return outer.unmodifiable();
    }

    private static boolean validateExtractFluidStatsElement(Object o) {
        if (!(o instanceof UnmodifiableConfig outer)) return false;
        Map<String, Object> outerMap = outer.valueMap();
        if (outerMap.isEmpty() || outerMap.size() != 1) return false;

        var entry = outerMap.entrySet().iterator().next();
        Object innerObj = entry.getValue();
        if (!(innerObj instanceof UnmodifiableConfig inner)) return false;

        Object n = inner.get("n");
        Object p = inner.get("p");
        Object k = inner.get("k");
        Object thc = inner.get("thc");
        Object cbd = inner.get("cbd");

        // thc/cbd are optional but if present must be int-like
        boolean okNpk = isIntLike(n) && isIntLike(p) && isIntLike(k);
        boolean okThc = (thc == null) || isIntLike(thc);
        boolean okCbd = (cbd == null) || isIntLike(cbd);
        return okNpk && okThc && okCbd;
    }

    @SubscribeEvent
    static void onConfigLoadOrReload(final ModConfigEvent event) {
        rebuildPlantNutrientsCache();
        // No explicit cache needed for EXTRACT_FLUID_STATS; it is read lazily by ExtractFluidStats.
    }
}
