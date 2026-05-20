package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;

/**
 * A {@link WeedFluidType} variant whose tint color is derived from the {@link StrainData}
 * stored on the {@link FluidStack} (via {@link StrainUtil}).
 */
public class MixtureWeedFluidType extends WeedFluidType {

    public MixtureWeedFluidType(ResourceLocation stillTexture,
                               ResourceLocation flowingTexture,
                               ResourceLocation overlayTexture,
                               int tintColor,
                               Vector3f fogColor,
                               Properties properties,
                               int iThc,
                               int iCbd,
                               String weedNamePart1,
                               String weedNamePart2) {
        super(stillTexture, flowingTexture, overlayTexture, tintColor, fogColor, properties, iThc, iCbd, weedNamePart1, weedNamePart2);
    }

    @Override
    public IClientFluidTypeExtensions getClientFluidTypeExtensions() {
        final IClientFluidTypeExtensions base = super.getClientFluidTypeExtensions();
        return new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return base.getStillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return base.getFlowingTexture();
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return base.getOverlayTexture();
            }

            @Override
            public Vector3f modifyFogColor(net.minecraft.client.Camera camera, float partialTick,
                                          net.minecraft.client.multiplayer.ClientLevel level, int renderDistance,
                                          float darkenWorldAmount, Vector3f fluidFogColor) {
                return base.modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);
            }

            @Override
            public void modifyFogRender(net.minecraft.client.Camera camera,
                                       net.minecraft.client.renderer.FogRenderer.FogMode mode,
                                       float renderDistance,
                                       float partialTick,
                                       float nearDistance,
                                       float farDistance,
                                       com.mojang.blaze3d.shaders.FogShape shape) {
                base.modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);
            }

            @Override
            public int getTintColor(FluidStack stack) {
                StrainData strain = StrainUtil.getStrain(stack);
                if (strain != StrainData.EMPTY) {
                    return applyThcVibrancy(strain.colorArgb(), strain.thc());
                }
                return base.getTintColor(stack);
            }

            @Override
            public int getTintColor() {
                return base.getTintColor();
            }
        };
    }

    // -----------------------------------------------------------------------
    // Description (fluid name in GUIs and tooltips)
    // -----------------------------------------------------------------------

    @Override
    public Component getDescription(FluidStack stack) {
        StrainData d = StrainUtil.getStrain(stack);
        if (d != StrainData.EMPTY && d.identified() && d.displayName() != null && !d.displayName().isBlank()) {
            return Component.literal(d.displayName() + " Oil");
        }
        return super.getDescription(stack);
    }

    // -----------------------------------------------------------------------
    // Bucket filling — copy StrainData + MIX_KEY from FluidStack to bucket ItemStack
    // -----------------------------------------------------------------------

    @Override
    public ItemStack getBucket(FluidStack stack) {
        ItemStack bucket = super.getBucket(stack);
        StrainData strain = StrainUtil.getStrain(stack);
        if (strain != StrainData.EMPTY) {
            bucket.set(ModDataComponentTypes.STRAIN_DATA.get(), strain);
        }
        String mixKey = stack.get(ModDataComponentTypes.MIX_KEY.get());
        if (mixKey != null && !mixKey.isBlank()) {
            bucket.set(ModDataComponentTypes.MIX_KEY.get(), mixKey);
        }
        return bucket;
    }

    // -----------------------------------------------------------------------
    // THC-based saturation vibrancy
    // -----------------------------------------------------------------------

    /**
     * Scales the color saturation based on THC value.
     * THC 0 → 50% saturation (desaturated), THC 30 → 100% saturation (full color).
     */
    private static int applyThcVibrancy(int argb, int thc) {
        float thcFactor = Math.min(1.0f, Math.max(0.0f, thc / 30.0f));
        float satScale = 0.5f + 0.5f * thcFactor;

        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        // Perceived luminance
        float luma = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        r = Math.clamp((int) (luma + satScale * (r - luma)), 0, 255);
        g = Math.clamp((int) (luma + satScale * (g - luma)), 0, 255);
        b = Math.clamp((int) (luma + satScale * (b - luma)), 0, 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
