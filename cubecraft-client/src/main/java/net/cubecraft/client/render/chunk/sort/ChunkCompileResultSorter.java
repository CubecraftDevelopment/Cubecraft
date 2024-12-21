package net.cubecraft.client.render.chunk.sort;

import me.gb2022.quantum3d.util.FrustumCuller;
import me.gb2022.quantum3d.util.camera.ViewFrustum;
import net.cubecraft.client.render.chunk.compile.ChunkCompileResult;
import org.joml.Vector3d;

import java.util.Comparator;

public final class ChunkCompileResultSorter extends DistanceSorter implements Comparator<ChunkCompileResult> {
    private final ViewFrustum frustum;

    public ChunkCompileResultSorter(ViewFrustum frustum) {
        this.frustum = frustum;
    }

    @Override
    public int compare(ChunkCompileResult o1, ChunkCompileResult o2) {
        int order = this.getOrderObject(o1, o2);
        if (order != -2) {
            return -order;
        }
        if (o1.failed()) {
            order = -1;
            return -order;
        }
        if (o2.failed()) {
            order = 1;
            return -order;
        }

        var x1 = o1.getX();
        var y1 = o1.getY();
        var z1 = o1.getZ();
        var x2 = o2.getX();
        var y2 = o2.getY();
        var z2 = o2.getZ();


        order = this.getOrderFrustum(this.frustum, x1, y1, z1, x2, y2, z2);
        if (order != 0) {
            return -order;
        }

        Vector3d camPos = this.getCameraPosition();
        return Double.compare(
                camPos.distance(x1 * 16 + 8, y1 * 16 + 8, z1 * 16 + 8),
                camPos.distance(x2 * 16 + 8, y2 * 16 + 8, z2 * 16 + 8)
        );
    }
}
