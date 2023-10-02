package ink.flybird.cubecraft.client.render.chunk.sort;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;

import java.util.Comparator;

public final class ChunkSorter extends DistanceSorter implements Comparator<RenderChunkPos> {
    @Override
    public int compare(RenderChunkPos o1, RenderChunkPos o2) {
        int i = this.getOrderObject(o1, o2);
        return i != -2 ? i : this.getOrderDistance(o1, o2);
    }
}
