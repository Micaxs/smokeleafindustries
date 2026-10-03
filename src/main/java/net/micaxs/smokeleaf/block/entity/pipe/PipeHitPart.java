package net.micaxs.smokeleaf.block.entity.pipe;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * One raycast-clickable region of a pipe block: either a type's small lane connector cube
 * ({@code direction == null}) or one of its existing connector arms. Used by the wrench to resolve
 * exactly which type/face a click landed on, the same way Modern Industrialization's
 * {@code PipeVoxelShape} drives its wrench (MIT licensed).
 */
public record PipeHitPart(PipeType type, @Nullable Direction direction, VoxelShape shape) {
}
