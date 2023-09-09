package ink.flybird.cubecraft.client.internal.renderer.world.chunk;

import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.culling.FrustumCuller;
import io.flybird.cubecraft.internal.entity.EntityPlayer;

import java.util.Comparator;

public final class ChunkSorter implements Comparator<RenderChunk> {
    private final FrustumCuller frustum;
    private final Camera camera;
    private final EntityPlayer player;

    public ChunkSorter(FrustumCuller frustum, Camera camera, EntityPlayer player) {
        this.frustum = frustum;
        this.camera = camera;
        this.player = player;
    }

    @Override
    public int compare(RenderChunk o1, RenderChunk o2) {
        if (!frustum.aabbVisible(o2.getVisibleArea(camera))) {
            return 1;
        }
        if (!frustum.aabbVisible(o1.getVisibleArea(camera))) {
            return -1;
        }
        return -Double.compare(o1.distanceTo(player), o2.distanceTo(player));
    }
}
