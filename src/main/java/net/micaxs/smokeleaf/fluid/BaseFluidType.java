package net.micaxs.smokeleaf.fluid;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BaseFluidType extends FluidType implements TintedFluidType {

    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;


    public BaseFluidType(final Identifier stillTexture, final Identifier flowingTexture, final Identifier overlayTexture, final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    @Override
    public Identifier getStillTexture() {
        return stillTexture;
    }

    @Override
    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    @Override
    public @Nullable Identifier getOverlayTexture() {
        return overlayTexture;
    }

    @Override
    public int getTintColor() {
        return tintColor;
    }

    @Override
    public Vector3f getFogColor() {
        return fogColor;
    }

}
