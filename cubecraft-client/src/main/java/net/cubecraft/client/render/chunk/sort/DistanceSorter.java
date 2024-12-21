package net.cubecraft.client.render.chunk.sort;

import me.gb2022.quantum3d.util.camera.ViewFrustum;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import org.joml.Vector3d;

public abstract class DistanceSorter {
    private final Vector3d cameraPosition = new Vector3d(0, 0, 0);

    public Vector3d getCameraPosition() {
        return cameraPosition;
    }

    public void setPos(Vector3d pos) {
        this.cameraPosition.set(pos);
    }

    public void setPos(double x, double y, double z) {
        this.cameraPosition.set(x, y, z);
    }

    public int getOrderFrustum(ViewFrustum frustum, int x1, int y1, int z1, int x2, int y2, int z2) {
        Vector3d camPos = this.getCameraPosition();
        if (!frustum.boxVisible(RenderChunkPos.getBounding(camPos, x1, y1, z1))) {
            return -1;
        }
        if (!frustum.boxVisible(RenderChunkPos.getBounding(camPos, x2, y2, z2))) {
            return 1;
        }
        return 0;
    }

    public int getOrderObject(Object o1, Object o2) {
        if (o1 == o2 && o1 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.equals(o2)) {
            return 0;
        }
        return -2;
    }
}
