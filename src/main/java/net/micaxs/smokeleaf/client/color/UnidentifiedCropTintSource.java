package net.micaxs.smokeleaf.client.color;

import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.block.entity.UnidentifiedWeedCropBlockEntity;
import net.micaxs.smokeleaf.strain.StrainData;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tints of the player-made strain crop: tint index 0 is the base leaf layer (strain leaf color),
 * index 1 the mask layer (strain color, in-world only). StrainData lives on the bottom half's block
 * entity; the top half has none, so it looks one block down.
 */
public record UnidentifiedCropTintSource(boolean mask) implements BlockTintSource {
    private static final int UNTINTED = 0xFFFFFFFF;
    private static final int FALLBACK_LEAF = 0xFF99D335;

    @Override
    public int color(BlockState state) {
        return mask ? UNTINTED : FALLBACK_LEAF;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof UnidentifiedWeedCropBlockEntity) && state.hasProperty(UnidentifiedWeedCropBlock.TOP)
                && Boolean.TRUE.equals(state.getValue(UnidentifiedWeedCropBlock.TOP))) {
            be = level.getBlockEntity(pos.below());
        }
        if (be instanceof UnidentifiedWeedCropBlockEntity crop) {
            StrainData d = crop.getStrain();
            if (d != null && d != StrainData.EMPTY) return mask ? d.colorArgb() : d.leafColor();
        }
        return color(state);
    }
}
