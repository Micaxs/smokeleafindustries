package net.micaxs.smokeleaf.block.entity.pipe;

/**
 * The three kinds of pipe that can coexist (up to one of each) at a single {@code PipeBlockEntity}
 * position. Ordinal is used directly as an array index throughout the pipe system — do not reorder.
 */
public enum PipeType {
    ITEM,
    FLUID,
    ENERGY;

    public static final PipeType[] VALUES = values();
}
