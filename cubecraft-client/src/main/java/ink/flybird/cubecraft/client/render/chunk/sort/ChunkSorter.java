package ink.flybird.cubecraft.client.render.chunk.sort;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;

import java.util.Comparator;

public final class ChunkSorter implements Comparator<RenderChunkPos> {
    private final FrustumCuller frustum;
    private final Camera camera;
    private final EntityPlayer player;

    public ChunkSorter(FrustumCuller frustum, Camera camera, EntityPlayer player) {
        this.frustum = frustum;
        this.camera = camera;
        this.player = player;
    }

    static int getVisibleOrderInAABB(FrustumCuller frustum, EntityPlayer player, Camera camera, RenderChunkPos pos, RenderChunkPos pos2) {
        return -Double.compare(pos2.distanceTo(player), pos.distanceTo(player));
    }

    @Override
    public int compare(RenderChunkPos o1, RenderChunkPos o2) {
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.equals(o2)) {
            return 0;
        }
        return -Double.compare(o1.distanceTo(player), o2.distanceTo(player));
    }
}
