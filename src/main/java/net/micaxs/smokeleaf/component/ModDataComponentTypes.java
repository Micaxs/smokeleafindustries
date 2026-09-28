package net.micaxs.smokeleaf.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import net.micaxs.smokeleaf.fluid.WeedFluidData;
import net.micaxs.smokeleaf.strain.StrainData;

/**
 * NBT-backed stand-ins for the 1.21 data components (see {@link DataKey}).
 */
public class ModDataComponentTypes {

    private static final Codec<JsonArray> JSON_ARRAY_CODEC = Codec.STRING.xmap(
            s -> {
                try {
                    return JsonParser.parseString(s).getAsJsonArray();
                } catch (Exception e) {
                    return new JsonArray();
                }
            },
            JsonArray::toString
    );


    // Weed Ingredients (Effects)
    public static final DataKey<String> ACTIVE_INGREDIENT = new DataKey<>("active_ingredient", Codec.STRING);
    public static final DataKey<JsonArray> ACTIVE_INGREDIENTS = new DataKey<>("active_ingredients", JSON_ARRAY_CODEC);
    public static final DataKey<Integer> EFFECT_DURATION = new DataKey<>("effect_duration", Codec.INT);

    // Weed Drying Time
    public static final DataKey<Boolean> DRY = new DataKey<>("dry", Codec.BOOL);
    public static final DataKey<Integer> DRYING_TIME = new DataKey<>("drying_time", Codec.INT);

    // Weed THC/CBD Content
    public static final DataKey<Integer> THC = new DataKey<>("thc", Codec.INT);
    public static final DataKey<Integer> CBD = new DataKey<>("cbd", Codec.INT);

    // Highest THC/CBD among the weeds rolled into a Blunt/Joint — used to pick the STONED trip
    // shader tier and CBD duration discount at consumption time. Kept separate from THC/CBD above
    // since those get silently overwritten by the generic crafting-preview strain copy hook.
    public static final DataKey<Integer> TRIP_THC = new DataKey<>("trip_thc", Codec.INT);
    public static final DataKey<Integer> TRIP_CBD = new DataKey<>("trip_cbd", Codec.INT);

    // Weed Extract Fluid payload (effects attached at machine-time to FluidStacks)
    public static final DataKey<WeedFluidData> WEED_FLUID_DATA = new DataKey<>("weed_fluid_data", WeedFluidData.CODEC);

    // Plant Nutrients (Increase THC with: 5N 10P 14K) (Increase CBD with: 8N 4P 13K) (Optimal pH: 6.0 - 7.0)
    public static final DataKey<Integer> PH = new DataKey<>("ph", Codec.INT);
    public static final DataKey<Integer> NITROGEN = new DataKey<>("nitrogen", Codec.INT);
    public static final DataKey<Integer> PHOSPHORUS = new DataKey<>("phosphorus", Codec.INT);
    public static final DataKey<Integer> POTASSIUM = new DataKey<>("potassium", Codec.INT);

    // DNA Contents
    public static final DataKey<DNAContents> DNA_CONTENTS = new DataKey<>("dna_contents", DNAContents.CODEC);


    // Manual Grinder stored contents (immutable)
    public static final DataKey<ManualGrinderContents> MANUAL_GRINDER_CONTENTS = new DataKey<>("manual_grinder_contents", ManualGrinderContents.CODEC);

    // Custom strain payload for player-created strains (items and mixture fluids)
    public static final DataKey<StrainData> STRAIN_DATA = new DataKey<>("strain_data", StrainData.CODEC);

    // Canonical mix key for mixer-produced strains (sorted "strainA||strainB"), used for server-wide naming
    public static final DataKey<String> MIX_KEY = new DataKey<>("mix_key", Codec.STRING);

    /** Universal strain lineage identifier (UUID string for mutator-created strains, MIX_KEY value for mixer-blended strains). */
    public static final DataKey<String> STRAIN_ID = new DataKey<>("strain_id", Codec.STRING);

    /** The player who first named / discovered this strain. Empty for preset/builtin strains. */
    public static final DataKey<String> STRAIN_CREATOR = new DataKey<>("strain_creator", Codec.STRING);

}
