package net.micaxs.smokeleaf.utils;

/**
 * Calculates the leaf-meter cost for a Strain Modifier preview operation.
 * Shared between client (cost preview) and server (validation before apply).
 *
 * <h3>Cost breakdown</h3>
 * <ul>
 *   <li><b>THC/CBD delta</b> – scaled by magnitude: 2/pt (0-5), 4/pt (5-10),
 *       8/pt (10-20), 12/pt (20+).</li>
 *   <li><b>Color change</b> – flat {@link #COLOR_CHANGE_COST} leaves if any of the
 *       6 leaf/strain colour channels differ from the original, regardless of how many.</li>
 *   <li><b>Name change</b> – flat {@link #NAME_CHANGE_COST} leaves if the (trimmed)
 *       name differs from the original.</li>
 * </ul>
 */
public final class StrainModifierCostUtil {

    public static final int COLOR_CHANGE_COST = 4;
    public static final int NAME_CHANGE_COST  = 4;

    private StrainModifierCostUtil() {}

    public static int calculateCost(
            int origThc,    int origCbd,
            int origLeafR,  int origLeafG,  int origLeafB,
            int origStrainR, int origStrainG, int origStrainB,
            int newThc,     int newCbd,
            int newLeafR,   int newLeafG,   int newLeafB,
            int newStrainR, int newStrainG, int newStrainB,
            String origName, String newName
    ) {
        int cost = 0;

        // Stat changes (scaled by magnitude)
        cost += statDeltaCost(Math.abs(newThc - origThc));
        cost += statDeltaCost(Math.abs(newCbd - origCbd));

        // Color changes — flat cost if anything moved, regardless of how much
        boolean colorChanged =
                newLeafR   != origLeafR   || newLeafG   != origLeafG   || newLeafB   != origLeafB ||
                newStrainR != origStrainR || newStrainG != origStrainG || newStrainB != origStrainB;
        if (colorChanged) cost += COLOR_CHANGE_COST;

        // Name change — flat cost if the trimmed name actually differs from the original
        String o = origName == null ? "" : origName.trim();
        String n = newName  == null ? "" : newName.trim();
        if (!o.equals(n)) cost += NAME_CHANGE_COST;

        return cost;
    }

    /**
     * Scaled cost for one stat (THC or CBD) based on magnitude of change from original.
     */
    public static int statDeltaCost(int delta) {
        int cost = 0;
        int d = Math.abs(delta);
        cost += Math.min(d, 5) * 2;
        d = Math.max(0, d - 5);
        cost += Math.min(d, 5) * 4;
        d = Math.max(0, d - 5);
        cost += Math.min(d, 10) * 8;
        d = Math.max(0, d - 10);
        cost += d * 12;
        return cost;
    }
}
