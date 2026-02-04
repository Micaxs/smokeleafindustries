package net.micaxs.smokeleaf.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Per-FluidStack payload for weed extract fluids.
 *
 * Stored as a Data Component on {@link net.neoforged.neoforge.fluids.FluidStack}.
 */
public record WeedFluidData(List<ResourceLocation> effects, int amplifier, int durationTicks) {

    public static final WeedFluidData EMPTY = new WeedFluidData(List.of(), 0, 0);

    public static final Codec<WeedFluidData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.listOf().fieldOf("effects").forGetter(WeedFluidData::effects),
            Codec.INT.fieldOf("amplifier").forGetter(WeedFluidData::amplifier),
            Codec.INT.fieldOf("duration").forGetter(WeedFluidData::durationTicks)
    ).apply(inst, WeedFluidData::new));
}
