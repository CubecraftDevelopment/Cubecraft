package net.cubecraft.client.render.chunk.status;


public interface ChunkStatusHandler extends ChunkStatusCache.UpdateHandler {
    void testFace(int x, int y, int z, boolean[] v);

    boolean testFrustum(int x, int y, int z);
}
