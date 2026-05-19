package net.micaxs.smokeleaf.strain;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Canonical preset registry for named strains during the Option C migration.
 *
 * <p>Per-strain items are being phased out in favour of generic items that carry
 * {@link StrainData}. This registry is the source of truth for known/default
 * named-strain payloads.</p>
 */
public final class StrainRegistry {
    private static final Map<String, StrainData> PRESETS = new LinkedHashMap<>();

    static {
        register("white_widow", preset(
                0xFFCAD9B3,
                15, 10,
                9, 6, 11,
                List.of(rl("minecraft", "wind_charged")),
                1,
                200,
                "White Widow"
        ));
        register("bubble_kush", preset(
                0xFFE3D292,
                20, 5,
                10, 8, 16,
                List.of(rl("minecraft", "strength")),
                1,
                180,
                "Bubble Kush"
        ));
        register("lemon_haze", preset(
                0xFFCDDC98,
                19, 6,
                9, 12, 15,
                List.of(rl("minecraft", "speed")),
                1,
                160,
                "Lemon Haze"
        ));
        register("sour_diesel", preset(
                0xFFFFBDB8,
                19, 6,
                11, 13, 10,
                List.of(rl("minecraft", "haste")),
                1,
                170,
                "Sour Diesel"
        ));
        register("blue_ice", preset(
                0xFFB6E9BA,
                20, 5,
                14, 9, 14,
                List.of(rl("minecraft", "night_vision")),
                1,
                190,
                "Blue Ice"
        ));
        register("bubblegum", preset(
                0xFFCAA375,
                17, 8,
                14, 14, 12,
                List.of(rl("minecraft", "health_boost")),
                1,
                150,
                "Bubblegum"
        ));
        register("purple_haze", preset(
                0xFFD481F3,
                16, 9,
                11, 13, 7,
                List.of(rl("minecraft", "luck")),
                1,
                140,
                "Purple Haze"
        ));
        register("og_kush", preset(
                0xFF91BB70,
                25, 10,
                12, 13, 15,
                List.of(rl("minecraft", "resistance")),
                1,
                210,
                "OG Kush"
        ));
        register("jack_herer", preset(
                0xFFDDD084,
                18, 7,
                10, 15, 10,
                List.of(rl("smokeleafindustries", "r_trees")),
                1,
                205,
                "Jack Herer"
        ));
        register("gary_peyton", preset(
                0xFFE3D3B6,
                22, 3,
                14, 15, 9,
                List.of(rl("smokeleafindustries", "uplifted")),
                1,
                195,
                "Gary Peyton"
        ));
        register("amnesia_haze", preset(
                0xFFA5B57D,
                19, 6,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "zombified")),
                1,
                185,
                "Amnesia Haze"
        ));
        register("ak47", preset(
                0xFFAFAE61,
                19, 6,
                17, 8, 11,
                List.of(rl("smokeleafindustries", "relaxed")),
                1,
                190,
                "AK-47"
        ));
        register("ghost_train", preset(
                0xFFFFF0D7,
                19, 6,
                13, 12, 10,
                List.of(rl("smokeleafindustries", "shy")),
                1,
                220,
                "Ghost Train"
        ));
        register("grape_ape", preset(
                0xFF98AE9E,
                18, 7,
                15, 13, 15,
                List.of(rl("smokeleafindustries", "aroused")),
                1,
                175,
                "Grape Ape"
        ));
        register("cotton_candy", preset(
                0xFFEFC5E1,
                19, 6,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "chillout")),
                1,
                165,
                "Cotton Candy"
        ));
        register("banana_kush", preset(
                0xFFD8D6A1,
                21, 4,
                15, 13, 15,
                List.of(rl("smokeleafindustries", "sticky_icky")),
                1,
                200,
                "Banana Kush"
        ));
        register("carbon_fiber", preset(
                0xFFA1A8A9,
                24, 1,
                14, 13, 20,
                List.of(rl("smokeleafindustries", "vein_high")),
                1,
                230,
                "Carbon Fiber"
        ));
        register("birthday_cake", preset(
                0xFFE9FDD1,
                23, 2,
                11, 13, 16,
                List.of(rl("minecraft", "oozing")),
                1,
                170,
                "Birthday Cake"
        ));
        register("blue_cookies", preset(
                0xFF9FB29F,
                17, 8,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "linguists_high")),
                1,
                180,
                "Blue Cookies"
        ));
        register("afghani", preset(
                0xFFFFCC95,
                18, 7,
                11, 10, 19,
                List.of(rl("minecraft", "bad_omen")),
                1,
                240,
                "Afghani"
        ));
        register("moonbow", preset(
                0xFFCDDE90,
                30, 13,
                15, 2, 22,
                List.of(rl("minecraft", "night_vision")),
                1,
                215,
                "Moonbow"
        ));
        register("lava_cake", preset(
                0xFFE5916C,
                22, 3,
                14, 12, 11,
                List.of(rl("minecraft", "glowing")),
                1,
                175,
                "Lava Cake"
        ));
        register("jelly_rancher", preset(
                0xFFFFF7B5,
                20, 5,
                11, 14, 9,
                List.of(rl("minecraft", "dolphins_grace")),
                1,
                165,
                "Jelly Rancher"
        ));
        register("strawberry_shortcake", preset(
                0xFFDBCDC6,
                16, 9,
                14, 11, 15,
                List.of(rl("smokeleafindustries", "high_flyer")),
                1,
                160,
                "Strawberry Shortcake"
        ));
        register("pink_kush", preset(
                0xFFBE9C9A,
                19, 6,
                17, 9, 12,
                List.of(rl("minecraft", "regeneration")),
                1,
                205,
                "Pink Kush"
        ));
    }

    private StrainRegistry() {
    }

    public static void register(String strainId, StrainData data) {
        PRESETS.put(normalize(strainId), data);
    }

    public static Optional<StrainData> get(String strainId) {
        return Optional.ofNullable(PRESETS.get(normalize(strainId)));
    }

    public static StrainData getRequired(String strainId) {
        return get(strainId).orElseThrow(() -> new IllegalArgumentException("Unknown strain preset: " + strainId));
    }

    public static boolean contains(String strainId) {
        return PRESETS.containsKey(normalize(strainId));
    }

    public static Set<String> ids() {
        return PRESETS.keySet();
    }

    private static StrainData preset(int colorArgb,
                                     int thc,
                                     int cbd,
                                     int nitrogen,
                                     int phosphorus,
                                     int potassium,
                                     List<ResourceLocation> effects,
                                     int amplifier,
                                     int durationTicks,
                                     String displayName) {
        return new StrainData(
                colorArgb,
                thc,
                cbd,
                nitrogen,
                phosphorus,
                potassium,
                List.copyOf(effects),
                amplifier,
                durationTicks,
                true,
                displayName
        );
    }

    private static String normalize(String strainId) {
        return strainId == null ? "" : strainId.trim().toLowerCase();
    }

    private static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
