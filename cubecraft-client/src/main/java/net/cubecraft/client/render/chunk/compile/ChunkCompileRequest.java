package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.World;

public final class ChunkCompileRequest {
    private final World world;
    private final int x;
    private final int y;
    private final int z;

    private final int[] layers;

    private ChunkCompileRequest(World world, int x, int y, int z, int[] layers) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.layers = layers;
    }

    public static ChunkCompileRequest build(World world, int x, int y, int z, int[] layers) {
        return new ChunkCompileRequest(world, x, y, z, layers);
    }

    public RenderChunkPos getPos() {
        return RenderChunkPos.create(this.x, this.y, this.z);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ChunkCompileRequest req)) {
            return false;
        }
        if (!req.world.equals(this.world)) {
            return false;
        }

        return !(this.x != req.x || this.y != req.y || this.z != req.z);
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int[] getLayers() {
        return this.layers;
    }
}
