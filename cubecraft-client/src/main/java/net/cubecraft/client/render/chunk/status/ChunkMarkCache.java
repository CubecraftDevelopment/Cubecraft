package net.cubecraft.client.render.chunk.status;

public interface ChunkMarkCache {
    void processUpdate();

    void moveTo(int centerX, int centerY, int centerZ);

    RenderChunkStatus get(int x, int y, int z);

    default void delete(){}

    default RenderChunkStatus getWithFallback(int x, int y, int z){
        return get(x, y, z);
    }

    interface UpdateHandler {
        boolean apply(int ax, int ay, int az);
    }
}
