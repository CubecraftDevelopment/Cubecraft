package net.cubecraft.client.render.chunk.status;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.cubecraft.client.render.chunk.ChunkLayer;
import net.cubecraft.client.render.chunk.TerrainRenderer;
import net.cubecraft.world.chunk.Chunk;

import java.util.ArrayList;

public class ModernChunkStatusCache implements ChunkMarkCache {
    private final TerrainRenderer renderer;
    private final UpdateHandler handler;
    private final Long2ObjectOpenHashMap<RenderChunkStatus> sectionMarks;
    private final int radiusH;
    private final int radiusV;

    private int centerX = Integer.MIN_VALUE + 1024;
    private int centerY = Integer.MIN_VALUE + 1024;
    private int centerZ = Integer.MIN_VALUE + 1024;
    private int updateCount;
    private int successUpdateCount;
    private int generateCount;


    public ModernChunkStatusCache(TerrainRenderer renderer) {
        this.renderer = renderer;
        this.handler = renderer;
        this.radiusH = renderer.getViewDistance();
        this.radiusV = Math.min((Chunk.SECTION_SIZE) / 2, renderer.getViewDistance());


        int dist = this.radiusH * 2 + 1;
        int distY = this.radiusV * 2 + 1;

        this.sectionMarks = new Long2ObjectOpenHashMap<>((int) (dist * dist * distY * 1.125));
    }


    @Override
    public void processUpdate() {
        this.updateCount = 0;
        this.successUpdateCount = 0;

        for (var status : new ArrayList<>(this.sectionMarks.values())) {
            var ax = status.getX();
            var ay = status.getY();
            var az = status.getZ();


            if (this.renderer.isChunkOutOfRange(ax, ay, az, 0)) {
                this.sectionMarks.remove(ChunkLayer.hash(ax, ay, az));
                continue;
            }

            if (get(ax, ay, az).getStatus() != ChunkUpdateStatus.UPDATE_REQUIRED) {
                continue;
            }

            this.updateCount++;
            if (this.handler.apply(ax, ay, az)) {
                this.get(ax, ay, az).setStatus(ChunkUpdateStatus.UPDATING);
                this.successUpdateCount++;
            }
        }
    }

    @Override
    public void moveTo(int centerX, int centerY, int centerZ) {
        if (centerX == this.centerX && centerY == this.centerY && centerZ == this.centerZ) {
            return;
        }

        var dx = centerX - this.centerX;
        var dy = centerY - this.centerY;
        var dz = centerZ - this.centerZ;

        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;

        this.generateCount = 0;

        for (var x = this.centerX - this.radiusH; x <= this.centerX + this.radiusH; x++) {
            for (var z = this.centerZ - this.radiusH; z <= this.centerZ + this.radiusH; z++) {
                for (var y = this.centerY - this.radiusH; y <= this.centerY + this.radiusV; y++) {
                    var distance = TerrainRenderer.chunkDistance(
                            this.centerX * 16 + 8,
                            this.centerY * 16 + 8,
                            this.centerZ * 16 + 8,
                            x, y, z
                    );
                    var dist = this.renderer.getViewDistance();
                    var key = ChunkLayer.hash(x, y, z);

                    if (distance >= dist) {
                        this.sectionMarks.remove(key);
                    } else if (!this.sectionMarks.containsKey(key)) {
                        this.sectionMarks.put(key, create(x, y, z));
                        this.generateCount++;
                    }
                }
            }
        }
    }

    public RenderChunkStatus create(int x, int y, int z) {
        return new RenderChunkStatus(this.renderer, x, y, z);
    }

    @Override
    public RenderChunkStatus getWithFallback(int x, int y, int z) {
        return this.sectionMarks.computeIfAbsent(ChunkLayer.hash(x, y, z), k -> create(x, y, z));
    }

    @Override
    public RenderChunkStatus get(int x, int y, int z) {
        return this.sectionMarks.get(ChunkLayer.hash(x, y, z));
    }
}
