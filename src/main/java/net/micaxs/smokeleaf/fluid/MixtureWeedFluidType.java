package net.micaxs.smokeleaf.fluid;

import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainUtil;
import net.minecraft.resources.ResourceLocation;
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
                    return strain.colorArgb();
                }
                return base.getTintColor(stack);
            }

            @Override
            public int getTintColor() {
                return base.getTintColor();
            }
        };
    }
}
