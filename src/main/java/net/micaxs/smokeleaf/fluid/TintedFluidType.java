package net.micaxs.smokeleaf.fluid;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Common-side description of how one of this mod's fluid types looks: textures, base tint, per-stack
 * tint and fog color. Kept free of client classes so server code (e.g. the Mixer deriving a blend
 * color) can read tints safely; {@code SmokeleafIndustriesClient} turns it into fluid models and
 * client extensions.
 */
public interface TintedFluidType {
    Identifier getStillTexture();

    Identifier getFlowingTexture();

    @Nullable
    Identifier getOverlayTexture();

    /** Base ARGB tint, used when no stack-specific data is available. */
    int getTintColor();

    /** ARGB tint for a specific stack; defaults to the base tint. */
    default int getTintColor(FluidStack stack) {
        return getTintColor();
    }

    Vector3f getFogColor();

    /** Tint of any fluid stack, or opaque white if its type isn't one of ours. */
    static int tintOf(FluidStack stack) {
        if (stack.isEmpty()) return 0xFFFFFFFF;
        FluidType type = stack.getFluidType();
        return type instanceof TintedFluidType tinted ? tinted.getTintColor(stack) : 0xFFFFFFFF;
    }
}
