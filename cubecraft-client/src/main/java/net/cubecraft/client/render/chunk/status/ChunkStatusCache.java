package net.cubecraft.client.render.chunk.status;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.TerrainRenderer;
import net.cubecraft.world.chunk.Chunk;

import java.util.Arrays;

public class ChunkStatusCache {
    private final TerrainRenderer renderer;

    private final RenderChunkStatus[] chunkStatusCache;
    private final RenderChunkStatus[] cacheCopy;
    private final int horizontalSize;
    private final int verticalSize;

    private final int horizontalRange;
    private final int verticalRange;

    private final UpdateHandler handler;

    private int centerX = Integer.MIN_VALUE + 1024;
    private int centerY = Integer.MIN_VALUE + 1024;
    private int centerZ = Integer.MIN_VALUE + 1024;

    private int updateCount;
    private int successUpdateCount;
    private int generateCount;

    public ChunkStatusCache(TerrainRenderer renderer) {
        this.renderer = renderer;
        this.handler = renderer;
        int dist = renderer.getViewDistance();
        int distY = Math.min(Chunk.SECTION_SIZE, dist);

        this.horizontalRange = dist;
        this.verticalRange = distY;
        this.horizontalSize = dist * 2 + 1;
        this.verticalSize = distY * 2 + 1;

        this.chunkStatusCache = new RenderChunkStatus[this.horizontalSize * this.verticalSize * this.horizontalSize];
        this.cacheCopy = new RenderChunkStatus[this.horizontalSize * this.verticalSize * this.horizontalSize];

        for (int x = 0; x < this.horizontalSize; x++) {
            for (int z = 0; z < this.horizontalSize; z++) {
                for (int y = 0; y < this.verticalSize; y++) {
                    toAbsY(y);
                    this.chunkStatusCache[toArrayPos(x, y, z)] = create(x, y, z);
                }
            }
        }
    }

    public void moveTo(int centerX, int centerY, int centerZ) {
        this.generateCount = 0;
        if (centerX == this.centerX && centerY == this.centerY && centerZ == this.centerZ) {
            return;
        }

        int offsetX = centerX - this.centerX;
        int offsetY = centerY - this.centerY;
        int offsetZ = centerZ - this.centerZ;

        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;

        System.arraycopy(this.chunkStatusCache, 0, this.cacheCopy, 0, this.horizontalSize * this.verticalSize * this.horizontalSize);

        for (int x = 0; x < this.horizontalSize; x++) {
            for (int y = 0; y < this.verticalSize; y++) {
                for (int z = 0; z < this.horizontalSize; z++) {
                    int newX = x + offsetX;
                    int newY = y + offsetY;
                    int newZ = z + offsetZ;

                    if (newX >= 0 && newX < this.horizontalSize && newY >= 0 && newY < this.verticalSize && newZ >= 0 && newZ < this.horizontalSize) {
                        double distance = RenderChunkPos.chunkDistanceTo(
                                toAbsX(x),
                                toAbsY(y),
                                toAbsZ(z),
                                RenderChunkPos.toWorldPos(this.centerX, this.centerY, this.centerZ)
                        );
                        int dist = this.renderer.getViewDistance() * 16;
                        if (distance >= dist) {
                            this.chunkStatusCache[toArrayPos(x, y, z)] = create(x, y, z);
                        } else {
                            this.chunkStatusCache[toArrayPos(x, y, z)] = this.cacheCopy[toArrayPos(newX, newY, newZ)];
                        }
                    } else {
                        toAbsY(y);
                        this.chunkStatusCache[toArrayPos(x, y, z)] = create(x, y, z);
                        this.generateCount++;
                    }
                }
            }
        }
    }

    public RenderChunkStatus create(int x, int y, int z) {
        return new RenderChunkStatus(this.renderer, toAbsX(x), toAbsY(y), toAbsZ(z));
    }

    public void set(RenderChunkPos pos, ChunkUpdateStatus status) {
        int x = toRelX(pos.getX());
        int y = toRelY(pos.getY());
        int z = toRelZ(pos.getZ());

        if (x < 0 || x >= this.horizontalSize) {
            return;
        }
        if (y < 0 || y >= this.verticalSize) {
            return;
        }
        if (z < 0 || z >= this.horizontalSize) {
            return;
        }

        this.chunkStatusCache[toArrayPos(x, y, z)].setStatus(status);
    }

    public void processUpdate() {
        this.updateCount = 0;
        this.successUpdateCount = 0;
        for (int x = 0; x < this.horizontalSize; x++) {
            for (int y = 0; y < this.verticalSize; y++) {
                for (int z = 0; z < this.horizontalSize; z++) {
                    if (this.chunkStatusCache[toArrayPos(x, y, z)].getStatus() != ChunkUpdateStatus.UPDATE_REQUIRED) {
                        continue;
                    }
                    RenderChunkPos pos = RenderChunkPos.create(toAbsX(x), toAbsY(y), toAbsZ(z));
                    double distance = pos.chunkDistanceTo(RenderChunkPos.toWorldPos(this.centerX, this.centerY, this.centerZ));
                    int dist = this.renderer.getViewDistance() * 16;
                    if (distance >= dist) {
                        continue;
                    }

                    this.updateCount++;
                    if (this.handler.apply(pos)) {
                        this.chunkStatusCache[toArrayPos(x, y, z)].setStatus(ChunkUpdateStatus.UPDATING);
                        this.successUpdateCount++;
                    }
                }
            }
        }
    }

    public int toAbsX(int x) {
        return x + this.centerX - this.horizontalRange;
    }

    public int toAbsY(int y) {
        return y + this.centerY - this.verticalRange;
    }

    public int toAbsZ(int z) {
        return z + this.centerZ - this.horizontalRange;
    }

    public int toRelX(long x) {
        return (int) (x - this.centerX + this.horizontalRange);
    }

    public int toRelY(long y) {
        return (int) (y - this.centerY + this.verticalRange);
    }

    public int toRelZ(long z) {
        return (int) (z - this.centerZ + this.horizontalRange);
    }


    @Override
    public String toString() {
        return "upd:%d upd_s:%d gen:%d".formatted(this.updateCount, this.successUpdateCount, this.generateCount);
    }

    public int toArrayPos(int x, int y, int z) {
        return (y * this.horizontalSize + z) * this.verticalSize + x;
    }

    public RenderChunkStatus get(int x, int y, int z) {
        var ptr=toArrayPos(toRelX(x), toRelY(y), toRelZ(z));

        if(ptr<0||ptr>chunkStatusCache.length) {
            return null;
        }

        return this.chunkStatusCache[ptr];
    }

    public void _delete(){
        Arrays.fill(this.chunkStatusCache, null);
        Arrays.fill(this.cacheCopy, null);
    }

    public interface UpdateHandler {
        boolean apply(RenderChunkPos pos);
    }
}
