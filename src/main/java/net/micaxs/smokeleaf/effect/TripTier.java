package net.micaxs.smokeleaf.effect;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.resources.ResourceLocation;

/**
 * The 12 "trip" visual tiers layered on top of {@link ModEffects#STONED}, selected by the THC
 * of whatever was smoked/eaten and encoded as the granted STONED {@code MobEffectInstance}'s
 * amplifier. Higher THC selects a more intense tier; the top 3 tiers (THC 26+) are intentionally
 * disorienting enough that the game is basically unplayable while they're active.
 *
 * <p>The shader/HUD badge only actually show once the player has used the same item (see
 * {@link TripStreakTracker}) at least {@link TripStreakTracker#REQUIRED_STREAK} times in a row —
 * gated via the effect's {@code visible} flag rather than the tier itself, so a single hit still
 * grants STONED normally, it just doesn't trip yet.
 */
public enum TripTier {
    MELLOW_BUZZ            ("mellow_buzz",             "Mellow Buzz",             false),
    COUCH_LOCK             ("couch_lock",               "Couch Lock",              false),
    BAKED_HAZE             ("baked_haze",               "Baked Haze",              false),
    GANJA_GOGGLES          ("ganja_goggles",            "Ganja Goggles",           false),
    DANK_WOBBLE            ("dank_wobble",               "Dank Wobble",             false),
    HOTBOX_VISION          ("hotbox_vision",             "Hotbox Vision",           false),
    TERPENE_TRAILS         ("terpene_trails",            "Terpene Trails",          false),
    STICKY_FINGERS_STATIC  ("sticky_fingers_static",     "Sticky Fingers Static",   false),
    BLAZED_BLOOM           ("blazed_bloom",              "Blazed Bloom",            false),
    KUSH_KALEIDOSCOPE      ("kush_kaleidoscope",         "Kush Kaleidoscope",       true),
    ASTRAL_VORTEX          ("astral_vortex",             "Astral Vortex",           true),
    COSMIC_COUCHLOCK       ("cosmic_couchlock",          "Cosmic Couchlock",        true);

    private static final TripTier[] VALUES = values();

    /** Inclusive upper THC bound for each tier in {@link #VALUES} order; last one catches up to 35. */
    private static final int[] THC_UPPER_BOUND = {4, 9, 13, 15, 17, 19, 21, 23, 25, 28, 31, 35};

    public final String id;
    public final String displayName;
    /** True for the 3 most intense tiers (THC 26+), where the shader makes the screen basically unplayable. */
    public final boolean unplayable;
    public final ResourceLocation shaderChain;
    public final ResourceLocation icon;

    TripTier(String id, String displayName, boolean unplayable) {
        this.id = id;
        this.displayName = displayName;
        this.unplayable = unplayable;
        this.shaderChain = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "shaders/post/" + id + ".json");
        this.icon = ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "textures/gui/trip/" + id + ".png");
    }

    public static TripTier byAmplifier(int amplifier) {
        return VALUES[Math.max(0, Math.min(VALUES.length - 1, amplifier))];
    }

    /** Maps THC (0-35) onto a tier — higher THC selects a more intense trip; 26+ is unplayable. */
    public static TripTier forThc(int thc) {
        int clamped = Math.max(0, Math.min(35, thc));
        for (int i = 0; i < THC_UPPER_BOUND.length; i++) {
            if (clamped <= THC_UPPER_BOUND[i]) return VALUES[i];
        }
        return VALUES[VALUES.length - 1];
    }

    /**
     * High CBD shortens the high: no change below 27, tapering down to 40% duration at CBD 30
     * (the top of the 0-30 CBD range).
     */
    public static float cbdDurationMultiplier(int cbd) {
        int c = Math.max(0, Math.min(30, cbd));
        if (c < 27) return 1.0f;
        float t = (c - 27) / 3.0f;
        return 1.0f - t * 0.6f;
    }
}
