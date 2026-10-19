package net.micaxs.smokeleaf.client.color;

import com.mojang.serialization.MapCodec;
import net.micaxs.smokeleaf.item.custom.DNAStrandItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Item model condition {@code smokeleafindustries:dna_full}: true when a DNA strand holds all its reagents. */
public record DnaFullProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<DnaFullProperty> MAP_CODEC = MapCodec.unit(new DnaFullProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return DNAStrandItem.isFull(stack);
    }

    @Override
    public MapCodec<DnaFullProperty> type() {
        return MAP_CODEC;
    }
}
