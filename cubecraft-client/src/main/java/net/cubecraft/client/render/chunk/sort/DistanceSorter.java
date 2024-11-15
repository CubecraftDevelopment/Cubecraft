package net.cubecraft.client.render.chunk.sort;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import me.gb2022.quantum3d.util.FrustumCuller;
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

    public int getOrderFrustum(FrustumCuller frustum, RenderChunkPos pos, RenderChunkPos pos2) {
        Vector3d camPos = this.getCameraPosition();
        if (!frustum.aabbVisible(pos.getBounding(camPos))) {
            return -1;
        }
        if (!frustum.aabbVisible(pos2.getBounding(camPos))) {
            return 1;
        }
        return 0;
    }

    public int getOrderDistance(RenderChunkPos pos, RenderChunkPos pos2) {
        Vector3d camPos = this.getCameraPosition();
        return -Double.compare(pos.distanceTo(camPos), pos2.distanceTo(camPos));
    }

    public int getOrderObject(Object o1,Object o2){
        if(o1==o2&&o1==null){
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
