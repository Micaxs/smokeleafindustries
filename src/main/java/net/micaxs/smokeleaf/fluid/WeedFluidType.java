package net.micaxs.smokeleaf.fluid;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class WeedFluidType extends FluidType implements TintedFluidType {

    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;

    private final int iThc;
    private final int iCbd;
    /** NBT key used to store effect ids on a FluidStack. Value: ListTag of StringTag resource locations. */
    public static final String NBT_EFFECTS = "Effects";
    /** NBT key used to store amplifier on a FluidStack (int). */
    public static final String NBT_AMPLIFIER = "Amplifier";
    /** NBT key used to store duration in ticks on a FluidStack (int). */
    public static final String NBT_DURATION_TICKS = "Duration";

    private final String[] weedNameParts = new String[2];

    public WeedFluidType(
            final Identifier stillTexture,
            final Identifier flowingTexture,
            final Identifier overlayTexture,
            final int tintColor,
            final Vector3f fogColor,
            final Properties properties,
            final int iThc,
            final int iCbd,
            final String weedNamePart1,
            final String weedNamePart2
        ) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
        this.iThc = iThc;
        this.iCbd = iCbd;
        this.weedNameParts[0] = weedNamePart1;
        this.weedNameParts[1] = weedNamePart2;
    }

    // Note: Effects are intended to be attached at machine-time to the produced FluidStack.
    // In NeoForge 21.1, FluidStack uses Data Components (not raw NBT tags) so effect data should
    // be modeled as a custom DataComponentType if you want it to persist/compare/serialize properly.

    /** THC value associated with this fluid type (static per registered type). */
    public int getThc() {
        return iThc;
    }

    /** CBD value associated with this fluid type (static per registered type). */
    public int getCbd() {
        return iCbd;
    }

    // ---- Legacy single-effect accessors (kept for compatibility with older call sites) ----
    /** First effect applied by this fluid type, or null if none. */
     public @Nullable MobEffect getEffect() {
        return null;
      }

      public int getEffectAmplifier() {
        return 0;
      }

      /** First effect duration in ticks, or 0 if none. */
      public int getEffectDuration() {
        return 0;
      }

    /** Two-part display name components you provided at construction time. */
    public String getWeedNamePart(int index) {
        return weedNameParts[index];
    }

    public String[] getWeedNameParts() {
        return weedNameParts.clone();
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
