package net.cubecraft.world.chunk.pos;

public class WorldChunkPos {
    private final ChunkPos pos;
    private final String world;

    public WorldChunkPos(String world, ChunkPos pos) {
        this.pos = pos;
        this.world = world;
    }

    public WorldChunkPos(String world, long x, long z) {
        this(world, ChunkPos.create(x, z));
    }

    public ChunkPos getPos() {
        return pos;
    }

    public String getWorld() {
        return world;
    }

    @Override
    public String toString() {
        return this.world + "@" + pos.toString();
    }
}
