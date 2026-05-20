package net.micaxs.smokeleaf.strain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Per-stack payload for player-created/custom strains.
 *
 * Stored on Items (seeds/buds/weed) and on custom mixture FluidStacks as a data component.
 *
 * Notes:
 * - "identified" controls whether the player has named it yet; MVP uses default display name.
 * - "displayName" can be empty when unidentified.
 */
public record StrainData(
        int colorArgb,
        int leafColor,
        int thc,
        int cbd,
        int nitrogen,
        int phosphorus,
        int potassium,
        List<ResourceLocation> effects,
        int amplifier,
        int durationTicks,
        boolean identified,
        String displayName,
        TypeColors typeColors
) {

    /** Per-item-type tint overrides; all-zero values fall back to the base bud colors. */
    public record TypeColors(
            int weedColorArgb,
            int weedLeafColor,
            int seedsColorArgb,
            int seedsLeafColor,
            int extractColorArgb,
            int extractLeafColor
    ) {
        public static final TypeColors NONE = new TypeColors(0, 0, 0, 0, 0, 0);

        static final MapCodec<TypeColors> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.optionalFieldOf("weed_color", 0).forGetter(TypeColors::weedColorArgb),
                Codec.INT.optionalFieldOf("weed_leaf_color", 0).forGetter(TypeColors::weedLeafColor),
                Codec.INT.optionalFieldOf("seeds_color", 0).forGetter(TypeColors::seedsColorArgb),
                Codec.INT.optionalFieldOf("seeds_leaf_color", 0).forGetter(TypeColors::seedsLeafColor),
                Codec.INT.optionalFieldOf("extract_color", 0).forGetter(TypeColors::extractColorArgb),
                Codec.INT.optionalFieldOf("extract_leaf_color", 0).forGetter(TypeColors::extractLeafColor)
        ).apply(inst, TypeColors::new));
    }

    /** Returns the weed highlight color, falling back to the base colorArgb. */
    public int weedColorArgbEffective()    { int v = typeColors.weedColorArgb();   return v != 0 ? v : colorArgb; }
    /** Returns the weed body color, falling back to the base leafColor. */
    public int weedLeafColorEffective()    { int v = typeColors.weedLeafColor();   return v != 0 ? v : leafColor; }
    /** Returns the seeds highlight color, falling back to the base colorArgb. */
    public int seedsColorArgbEffective()   { int v = typeColors.seedsColorArgb();  return v != 0 ? v : colorArgb; }
    /** Returns the seeds body color, falling back to the base leafColor. */
    public int seedsLeafColorEffective()   { int v = typeColors.seedsLeafColor();  return v != 0 ? v : leafColor; }
    /** Returns the extract highlight color, falling back to the base colorArgb. */
    public int extractColorArgbEffective() { int v = typeColors.extractColorArgb();return v != 0 ? v : colorArgb; }
    /** Returns the extract body color, falling back to the base leafColor. */
    public int extractLeafColorEffective() { int v = typeColors.extractLeafColor();return v != 0 ? v : leafColor; }

    public static final StrainData EMPTY = new StrainData(
            0xFFFFFFFF, 0xFF4A7A2E,
            0, 0, 0, 0, 0,
            List.of(), 0, 0,
            false, "",
            TypeColors.NONE
    );

    public static final Codec<StrainData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("color").forGetter(StrainData::colorArgb),
            Codec.INT.optionalFieldOf("leaf_color", 0xFF4A7A2E).forGetter(StrainData::leafColor),
            Codec.INT.fieldOf("thc").forGetter(StrainData::thc),
            Codec.INT.fieldOf("cbd").forGetter(StrainData::cbd),
            Codec.INT.fieldOf("n").forGetter(StrainData::nitrogen),
            Codec.INT.fieldOf("p").forGetter(StrainData::phosphorus),
            Codec.INT.fieldOf("k").forGetter(StrainData::potassium),
            ResourceLocation.CODEC.listOf().fieldOf("effects").forGetter(StrainData::effects),
            Codec.INT.fieldOf("amp").forGetter(StrainData::amplifier),
            Codec.INT.fieldOf("dur").forGetter(StrainData::durationTicks),
            Codec.BOOL.fieldOf("identified").forGetter(StrainData::identified),
            Codec.STRING.fieldOf("name").forGetter(StrainData::displayName),
            TypeColors.MAP_CODEC.forGetter(StrainData::typeColors)
    ).apply(inst, StrainData::new));
}

