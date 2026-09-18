package net.micaxs.smokeleaf.client.model;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * Custom geometry for the pipe block. Baking here only resolves the (statically known) type
 * textures and hands off to {@link PipeBakedModel}, which builds the actual hub/arm cuboids
 * per-instance from {@code PipeRenderState} every time the game asks for quads — cheap and only
 * invoked on a real chunk remesh (i.e. when a connection actually changes), never per frame.
 */
public class PipeUnbakedGeometry implements IUnbakedGeometry<PipeUnbakedGeometry> {
    public static final PipeUnbakedGeometry INSTANCE = new PipeUnbakedGeometry();

    public static final Material ITEM_MATERIAL = blockMaterial("block/pipe_item");
    public static final Material FLUID_MATERIAL = blockMaterial("block/pipe_fluid");
    public static final Material ENERGY_MATERIAL = blockMaterial("block/pipe_energy");

    private PipeUnbakedGeometry() {}

    private static Material blockMaterial(String path) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, path));
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        TextureAtlasSprite particleIcon = spriteGetter.apply(ITEM_MATERIAL);
        return new PipeBakedModel(spriteGetter, modelState, overrides, particleIcon);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        // No nested BlockModel parents to resolve — geometry is fully procedural.
    }
}
