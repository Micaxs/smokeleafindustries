package net.micaxs.smokeleaf.block.entity.pipe;

/**
 * Per-direction connection state for one {@link PipeType} segment of a {@code PipeBlockEntity}.
 * Ordinal is packed 2 bits per direction into NBT — do not reorder without a migration.
 */
public enum PipeConnection {
    /** Capped off — no connection on this face. */
    NONE,
    /** Connects to another pipe of the same type on this face, joining the same network. */
    PIPE,
    /** Pulls from the adjacent capability provider (machine/inventory/tank) into the network. */
    IMPORT,
    /** Pushes from the network into the adjacent capability provider. */
    EXPORT;

    private static final PipeConnection[] VALUES = values();

    public static PipeConnection byOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    public boolean isEndpoint() {
        return this == IMPORT || this == EXPORT;
    }
}
