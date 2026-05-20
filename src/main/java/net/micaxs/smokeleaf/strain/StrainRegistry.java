package net.micaxs.smokeleaf.strain;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.Registry;
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
 *
 * <p>Built-in datapack definitions live under
 * {@code data/smokeleafindustries/smokeleafindustries/strain/}.</p>
 */
public final class StrainRegistry {
    private static final Map<String, StrainData> PRESETS = new LinkedHashMap<>();
    private static final Map<String, StrainData> BUILTIN_PRESETS = new LinkedHashMap<>();

    static {
        register("white_widow", preset(
                0xFFBAC2A9, 0xFFB7D977,
                0xFFB1BA9F, 0xFF99B463,
                0, 0,
                0xFFD9E9BF, 0xFFFFF0CC,
                15, 10,
                9, 6, 11,
                List.of(rl("minecraft", "wind_charged")),
                1, 200,
                "White Widow"
        ));
        register("bubble_kush", preset(
                0xFFC2A756, 0xFFD7D04E,
                0xFF82772F, 0xFFD1C350,
                0, 0,
                0xFFF6E199, 0xFFFFEDC3,
                20, 5,
                10, 8, 16,
                List.of(rl("minecraft", "strength")),
                1, 180,
                "Bubble Kush"
        ));
        register("lemon_haze", preset(
                0xFFB7C558, 0xFFB1E058,
                0xFFAFBC57, 0xFF93BB44,
                0, 0,
                0xFFDEEDA0, 0xFFFFF1BF,
                19, 6,
                9, 12, 15,
                List.of(rl("minecraft", "speed")),
                1, 160,
                "Lemon Haze"
        ));
        register("sour_diesel", preset(
                0xFFFF797B, 0xFFE2B7A0,
                0xFFB16661, 0xFFE39C8E,
                0, 0,
                0xFFFFCAC4, 0xFFFFE8D4,
                19, 6,
                11, 13, 10,
                List.of(rl("minecraft", "haste")),
                1, 170,
                "Sour Diesel"
        ));
        register("blue_ice", preset(
                0xFF89C59F, 0xFFB0E05B,
                0xFF628444, 0xFFA5DB7B,
                0, 0,
                0xFFC4F5B9, 0xFFFFF3CB,
                20, 5,
                14, 9, 14,
                List.of(rl("minecraft", "night_vision")),
                1, 190,
                "Blue Ice"
        ));
        register("bubblegum", preset(
                0xFFC18359, 0xFFD4D825,
                0xFFB98352, 0xFFB0B01D,
                0, 0,
                0xFFFFD9A4, 0xFFFFEBC8,
                17, 8,
                14, 14, 12,
                List.of(rl("minecraft", "health_boost")),
                1, 150,
                "Bubblegum"
        ));
        register("purple_haze", preset(
                0xFFE179FF, 0xFFE0B7A8,
                0xFFD675F6, 0xFFBC9690,
                0, 0,
                0xFFFFC7FF, 0xFFFFE7DF,
                16, 9,
                11, 13, 7,
                List.of(rl("minecraft", "luck")),
                1, 140,
                "Purple Haze"
        ));
        register("og_kush", preset(
                0xFFADDE88, 0xFFC5D853,
                0xFFA6D380, 0xFFA3B544,
                0, 0,
                0xFFD2F3A1, 0xFFFFF2C3,
                25, 10,
                12, 13, 15,
                List.of(rl("minecraft", "resistance")),
                1, 210,
                "OG Kush"
        ));
        register("jack_herer", preset(
                0xFFDEC38B, 0xFFE4D419,
                0xFFD3BA81, 0xFFBEB018,
                0, 0,
                0xFFF7E09D, 0xFFFFECC3,
                18, 7,
                10, 15, 10,
                List.of(rl("smokeleafindustries", "r_trees")),
                1, 205,
                "Jack Herer"
        ));
        register("gary_peyton", preset(
                0xFFD6C4A2, 0xFFB9D977,
                0xFFCBBC98, 0xFF9BB462,
                0, 0,
                0xFFE8E3B9, 0xFFFFEDCA,
                22, 3,
                14, 15, 9,
                List.of(rl("smokeleafindustries", "uplifted")),
                1, 195,
                "Gary Peyton"
        ));
        register("amnesia_haze", preset(
                0xFFC7DA89, 0xFFB8DA75,
                0xFFBDCF81, 0xFF9AB660,
                0, 0,
                0xFFDAEDAC, 0xFFFFF1C3,
                19, 6,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "zombified")),
                1, 185,
                "Amnesia Haze"
        ));
        register("ak47", preset(
                0xFFD2D77F, 0xFFEAC848,
                0xFFC9CC76, 0xFFC4A83A,
                0, 0,
                0xFFEFE694, 0xFFFFEEBA,
                19, 6,
                17, 8, 11,
                List.of(rl("smokeleafindustries", "relaxed")),
                1, 190,
                "AK-47"
        ));
        register("ghost_train", preset(
                0xFFCEBA9B, 0xFFFFBB42,
                0xFFC7B290, 0xFFDA9B36,
                0, 0,
                0xFFFBDCA7, 0xFFFFEAC7,
                19, 6,
                13, 12, 10,
                List.of(rl("smokeleafindustries", "shy")),
                1, 220,
                "Ghost Train"
        ));
        register("grape_ape", preset(
                0xFFA6D0BB, 0xFFD6C29E,
                0xFFA0C5B1, 0xFFB1A283,
                0, 0,
                0xFFD8E6D3, 0xFFFFEED4,
                18, 7,
                15, 13, 15,
                List.of(rl("smokeleafindustries", "aroused")),
                1, 175,
                "Grape Ape"
        ));
        register("cotton_candy", preset(
                0xFFD797CB, 0xFFC4C898,
                0xFFCC91BF, 0xFFA4A680,
                0, 0,
                0xFFF2D7DF, 0xFFFFE9D8,
                19, 6,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "chillout")),
                1, 165,
                "Cotton Candy"
        ));
        register("banana_kush", preset(
                0xFFE1CE7A, 0xFFB9CDAB,
                0xFFD4C475, 0xFF9CAC8C,
                0, 0,
                0xFFE5E6B2, 0xFFFFEEC8,
                21, 4,
                15, 13, 15,
                List.of(rl("smokeleafindustries", "sticky_icky")),
                1, 200,
                "Banana Kush"
        ));
        register("carbon_fiber", preset(
                0xFFA1B8D0, 0xFFBAC6C5,
                0xFF9AB0C5, 0xFF9AA4A5,
                0, 0,
                0xFFD4E4EB, 0xFFFFEDDF,
                24, 1,
                14, 13, 20,
                List.of(rl("smokeleafindustries", "vein_high")),
                1, 230,
                "Carbon Fiber"
        ));
        register("birthday_cake", preset(
                0xFFAAD198, 0xFFC5CB93,
                0xFFA3C691, 0xFFA3AA7B,
                0, 0,
                0xFFD7EBBC, 0xFFFFF0CB,
                23, 2,
                11, 13, 16,
                List.of(rl("minecraft", "oozing")),
                1, 170,
                "Birthday Cake"
        ));
        register("blue_cookies", preset(
                0xFF84B7B9, 0xFFC2CA95,
                0xFF7FAFAF, 0xFFA0A87D,
                0, 0,
                0xFFD4E8D2, 0xFFFFEFD4,
                17, 8,
                10, 13, 9,
                List.of(rl("smokeleafindustries", "linguists_high")),
                1, 180,
                "Blue Cookies"
        ));
        register("afghani", preset(
                0xFFD5915E, 0xFFEBC445,
                0xFFCC8C58, 0xFFC5A238,
                0, 0,
                0xFFFFDD99, 0xFFFFEBC6,
                18, 7,
                11, 10, 19,
                List.of(rl("minecraft", "bad_omen")),
                1, 240,
                "Afghani"
        ));
        register("moonbow", preset(
                0xFFC1D97C, 0xFFC3E12C,
                0xFFB8CE73, 0xFFA2BC25,
                0, 0,
                0xFFDCF097, 0xFFFFF2BC,
                30, 13,
                15, 2, 22,
                List.of(rl("minecraft", "night_vision")),
                1, 215,
                "Moonbow"
        ));
        register("lava_cake", preset(
                0xFFE27661, 0xFFC4A017,
                0xFF945931, 0xFFBB8A25,
                0, 0,
                0xFFFFD495, 0xFFFFEACA,
                22, 3,
                14, 12, 11,
                List.of(rl("minecraft", "glowing")),
                1, 175,
                "Lava Cake"
        ));
        register("jelly_rancher", preset(
                0xFFDACE72, 0xFFD5B51B,
                0xFF90822D, 0xFFD4BD3F,
                0, 0,
                0xFFF7E48C, 0xFFFFEDBD,
                20, 5,
                11, 14, 9,
                List.of(rl("minecraft", "dolphins_grace")),
                1, 165,
                "Jelly Rancher"
        ));
        register("strawberry_shortcake", preset(
                0xFF93A6C9, 0xFFC4A27C,
                0xFF808695, 0xFFA99A92,
                0, 0,
                0xFFEBDCD5, 0xFFFFEAD7,
                16, 9,
                14, 11, 15,
                List.of(rl("smokeleafindustries", "high_flyer")),
                1, 160,
                "Strawberry Shortcake"
        ));
        register("pink_kush", preset(
                0xFFD3A8A7, 0xFFDED713,
                0xFF8B7B3C, 0xFFD7C349,
                0, 0,
                0xFFF2E39B, 0xFFFFEDC1,
                19, 6,
                17, 9, 12,
                List.of(rl("minecraft", "regeneration")),
                1, 205,
                "Pink Kush"
        ));

        BUILTIN_PRESETS.putAll(PRESETS);
    }

    private StrainRegistry() {
    }

    public static void register(String strainId, StrainData data) {
        PRESETS.put(normalize(strainId), data);
    }

    public static void reload(Registry<StrainData> registry) {
        PRESETS.clear();
        PRESETS.putAll(BUILTIN_PRESETS);
        registry.entrySet().forEach(entry -> {
            String id = entry.getKey().location().getPath();
            PRESETS.put(normalize(id), entry.getValue());
        });
        SmokeleafIndustries.LOGGER.info("StrainRegistry reloaded: {} strains available", PRESETS.size());
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
                                     int leafColor,
                                     int weedColorArgb,
                                     int weedLeafColor,
                                     int seedsColorArgb,
                                     int seedsLeafColor,
                                     int extractColorArgb,
                                     int extractLeafColor,
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
                colorArgb, leafColor,
                thc, cbd,
                nitrogen, phosphorus, potassium,
                List.copyOf(effects),
                amplifier, durationTicks,
                true, displayName,
                new StrainData.TypeColors(weedColorArgb, weedLeafColor, seedsColorArgb, seedsLeafColor, extractColorArgb, extractLeafColor)
        );
    }

    private static String normalize(String strainId) {
        return strainId == null ? "" : strainId.trim().toLowerCase();
    }

    private static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
