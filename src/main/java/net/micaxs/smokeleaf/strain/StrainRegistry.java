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
                7, 10, 12,
                List.of(rl("minecraft", "speed")),
                1,
                160,
                "Lemon Haze"
        ));

        // TODO: Add the remaining named strains here as the full migration continues.
        // Suggested source values:
        // - THC/CBD and NPK from ModBlocks/ModItems grow + consumable defaults
        // - colorArgb from ModFluidTypes extract tints
        // - effect/duration/amplifier from the legacy named weed/extract items
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
