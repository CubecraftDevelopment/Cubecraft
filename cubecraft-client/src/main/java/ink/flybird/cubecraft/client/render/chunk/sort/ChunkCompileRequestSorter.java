package ink.flybird.cubecraft.client.render.chunk.sort;

import ink.flybird.cubecraft.client.render.chunk.compile.ChunkCompileRequest;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import org.joml.Vector3d;

import java.util.Comparator;

public final class ChunkCompileRequestSorter extends DistanceSorter implements Comparator<ChunkCompileRequest> {
    private final FrustumCuller frustum;

    public ChunkCompileRequestSorter(FrustumCuller frustum) {
        this.frustum = frustum;
    }

    @Override
    public int compare(ChunkCompileRequest o1, ChunkCompileRequest o2) {
        int order = this.getOrderObject(o1, o2);
        if (order != -2) {
            return -order;
        }

        order=this.getOrderFrustum(this.frustum, o1.getPos(), o2.getPos());
        if (order != 0) {
            return -order;
        }

        Vector3d camPos = this.getCameraPosition();
        order =  -Double.compare(o1.getPos().distanceTo(camPos), o2.getPos().distanceTo(camPos));
        return -order;
    }
}
