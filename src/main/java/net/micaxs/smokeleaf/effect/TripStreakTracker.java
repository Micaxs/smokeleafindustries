package net.micaxs.smokeleaf.effect;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Tracks how many times in a row a player has smoked/eaten the "same" item, server-side, in the
 * player's persistent data (the same {@code getPersistentData()} store the mod already uses for
 * strain-book discoveries). The trip shader/HUD only actually show once {@link #REQUIRED_STREAK}
 * consecutive uses of the same strain have been reached — every use before that still grants
 * STONED normally, it just doesn't trip yet. Uses after the streak is reached just keep adding
 * duration (see the STONED grant sites), they don't need to re-reach the streak.
 */
public final class TripStreakTracker {
    private TripStreakTracker() {}

    public static final int REQUIRED_STREAK = 3;

    private static final String TAG_LAST_KEY = "smokeleafindustries:last_trip_key";
    private static final String TAG_STREAK = "smokeleafindustries:trip_streak";

    /** Stable identity key for "is this the same item as last time" — prefers STRAIN_ID, then THC/CBD, then item id. */
    public static String keyFor(ItemStack stack, StrainData strain) {
        String sid = stack.get(ModDataComponentTypes.STRAIN_ID.get());
        if (sid != null && !sid.isBlank()) return sid;
        if (strain != null && strain != StrainData.EMPTY) return strain.thc() + "_" + strain.cbd();
        return stack.getItem().toString();
    }

    /** Convenience overload for Blunt/Joint, which store their rolled-up THC/CBD directly rather than full StrainData. */
    public static String keyForTripStats(int thc, int cbd) {
        return "trip_" + thc + "_" + cbd;
    }

    /** Registers one use of {@code key} and returns the new consecutive-use streak (1 if it broke a different streak). */
    public static int registerUseAndGetStreak(Player player, String key) {
        CompoundTag data = player.getPersistentData();
        String lastKey = data.getString(TAG_LAST_KEY);
        int streak = key.equals(lastKey) ? data.getInt(TAG_STREAK) + 1 : 1;
        data.putString(TAG_LAST_KEY, key);
        data.putInt(TAG_STREAK, streak);
        return streak;
    }
}
