package net.cubecraft.world.worldGen.pipeline.cache;

import net.cubecraft.world.chunk.Chunk;

import java.util.Arrays;

public final class HeightMap {
    private final double[] storage = new double[Chunk.WIDTH * Chunk.WIDTH];

    public HeightMap() {
        Arrays.fill(this.storage, 4);
    }

    public void setValue(int x, int z, double val) {
        this.storage[x * 16 + z] = val;
    }

    public int sample(int x, int z) {
        return (int) this.storage[x * 16 + z];
    }
}
