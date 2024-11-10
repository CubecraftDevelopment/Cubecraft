package net.cubecraft.client.render.chunk.sort;

import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import net.cubecraft.client.render.chunk.compile.ChunkCompileResult;
import org.joml.Vector3d;

import java.util.Comparator;

public final class ChunkCompileResultSorter extends DistanceSorter implements Comparator<ChunkCompileResult> {
    private final FrustumCuller frustum;

    public ChunkCompileResultSorter(FrustumCuller frustum) {
        this.frustum = frustum;
    }

    @Override
    public int compare(ChunkCompileResult o1, ChunkCompileResult o2) {
        int order = this.getOrderObject(o1, o2);
        if (order != -2) {
            return -order;
        }
        if (!o1.success()) {
            order = -1;
            return -order;
        }
        if (!o2.success()) {
            order = 1;
            return -order;
        }

        order = this.getOrderFrustum(this.frustum, o1.getPos(), o2.getPos());
        if (order != 0) {
            return -order;
        }

        Vector3d camPos = this.getCameraPosition();
        return Double.compare(o1.getPos().distanceTo(camPos), o2.getPos().distanceTo(camPos));
    }
}
