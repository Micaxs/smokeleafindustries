package net.micaxs.smokeleaf.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class WeedFluidType extends FluidType {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
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
            final ResourceLocation stillTexture,
            final ResourceLocation flowingTexture,
            final ResourceLocation overlayTexture,
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

    public IClientFluidTypeExtensions getClientFluidTypeExtensions() {
        return new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return fogColor;
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
            }
        };
    }

}
