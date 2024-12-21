package net.cubecraft.client.render.chunk.sort;

import net.cubecraft.client.render.chunk.ChunkLayer;
import net.cubecraft.client.render.chunk.RenderChunkPos;

import java.util.Comparator;

public final class ChunkSorter extends DistanceSorter implements Comparator<ChunkLayer> {
    @Override
    public int compare(ChunkLayer o1, ChunkLayer o2) {
        var x1 = o1.getOwner().getX();
        var y1 = o1.getOwner().getY();
        var z1 = o1.getOwner().getZ();
        var x2 = o2.getOwner().getX();
        var y2 = o2.getOwner().getY();
        var z2 = o2.getOwner().getZ();

        var camPos = this.getCameraPosition();

        return -Double.compare(
                camPos.distance(x1 * 16 + 8, y1 * 16 + 8, z1 * 16 + 8),
                camPos.distance(x2 * 16 + 8, y2 * 16 + 8, z2 * 16 + 8)
        );
    }
}
