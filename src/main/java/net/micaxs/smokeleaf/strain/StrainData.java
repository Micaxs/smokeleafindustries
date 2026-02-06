package net.micaxs.smokeleaf.strain;

import com.mojang.serialization.Codec;
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
        int thc,
        int cbd,
        int nitrogen,
        int phosphorus,
        int potassium,
        List<ResourceLocation> effects,
        int amplifier,
        int durationTicks,
        boolean identified,
        String displayName
) {

    public static final StrainData EMPTY = new StrainData(
            0xFFFFFFFF,
            0, 0,
            0, 0, 0,
            List.of(),
            0,
            0,
            false,
            ""
    );

    public static final Codec<StrainData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("color").forGetter(StrainData::colorArgb),
            Codec.INT.fieldOf("thc").forGetter(StrainData::thc),
            Codec.INT.fieldOf("cbd").forGetter(StrainData::cbd),
            Codec.INT.fieldOf("n").forGetter(StrainData::nitrogen),
            Codec.INT.fieldOf("p").forGetter(StrainData::phosphorus),
            Codec.INT.fieldOf("k").forGetter(StrainData::potassium),
            ResourceLocation.CODEC.listOf().fieldOf("effects").forGetter(StrainData::effects),
            Codec.INT.fieldOf("amp").forGetter(StrainData::amplifier),
            Codec.INT.fieldOf("dur").forGetter(StrainData::durationTicks),
            Codec.BOOL.fieldOf("identified").forGetter(StrainData::identified),
            Codec.STRING.fieldOf("name").forGetter(StrainData::displayName)
    ).apply(inst, StrainData::new));
}
