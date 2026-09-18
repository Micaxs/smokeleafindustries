package net.micaxs.smokeleaf.block.entity.pipe;

/**
 * Immutable snapshot of a {@code PipeBlockEntity}'s presence/connection state, passed to the
 * renderer via {@code ModelData} instead of {@code BlockState} (see the pipe system plan for why:
 * putting 3 types × 6 directions of connection state into real BlockState properties would blow up
 * the state table combinatorially). Defensive copies are taken so the renderer thread never sees a
 * half-mutated array.
 */
public final class PipeRenderState {
    public final boolean[] present;
    public final PipeConnection[][] connections;

    public PipeRenderState(boolean[] present, PipeConnection[][] connections) {
        this.present = present.clone();
        this.connections = new PipeConnection[connections.length][];
        for (int i = 0; i < connections.length; i++) {
            this.connections[i] = connections[i].clone();
        }
    }

    public static final PipeRenderState EMPTY = new PipeRenderState(new boolean[3], new PipeConnection[][]{
            {PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE},
            {PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE},
            {PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE, PipeConnection.NONE}
    });
}
