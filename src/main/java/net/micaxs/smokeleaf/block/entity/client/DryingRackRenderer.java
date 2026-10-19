package net.micaxs.smokeleaf.block.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.micaxs.smokeleaf.block.entity.DryingRackBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity, DryingRackRenderer.State> {

    // Slight downward adjustment
    private static final float Y_OFFSET_ADJUST = 0.05f;

    private final ItemModelResolver itemModelResolver;

    public DryingRackRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        final ItemStackRenderState[] items = new ItemStackRenderState[DryingRackBlockEntity.SLOT_COUNT];
        final float[][] offsets = new float[DryingRackBlockEntity.SLOT_COUNT][];

        State() {
            for (int i = 0; i < items.length; i++) items[i] = new ItemStackRenderState();
        }
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(DryingRackBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int seed = (int) be.getBlockPos().asLong();
        for (int slot = 0; slot < DryingRackBlockEntity.SLOT_COUNT; slot++) {
            ItemStack stack = be.getItem(slot);
            itemModelResolver.updateForTopItem(state.items[slot], stack, ItemDisplayContext.FIXED, be.getLevel(), null, seed + slot);
            state.offsets[slot] = be.getSlotRenderPosition(slot, facing);
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int slot = 0; slot < DryingRackBlockEntity.SLOT_COUNT; slot++) {
            ItemStackRenderState item = state.items[slot];
            if (item.isEmpty()) continue;

            float[] off = state.offsets[slot];
            pose.pushPose();
            // Center + offset (lowered slightly)
            pose.translate(0.5 + off[0], off[1] - Y_OFFSET_ADJUST, 0.5 + off[2]);
            // Lay flat
            pose.mulPose(Axis.XP.rotationDegrees(90f));
            // Scale
            pose.scale(0.4f, 0.4f, 0.4f);
            item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            pose.popPose();
        }
    }
}
