package net.cubecraft.world.chunk;

public enum ChunkState {
    EMPTY,
    TERRAIN,
    STRUCTURE,
    NEIGHBOR_STRUCTURE,
    COMPLETE;

    public boolean isComplete(ChunkState chunkState) {
        return this.ordinal()>=chunkState.ordinal();
    }
}
