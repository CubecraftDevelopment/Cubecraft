package net.cubecraft.client.render;

import org.joml.Vector3d;

import java.util.Comparator;

public class WorldRenderObjectSorter implements Comparator<WorldRenderObject> {
    private final Vector3d position = new Vector3d();

    @Override
    public int compare(WorldRenderObject o1, WorldRenderObject o2) {
        return -Double.compare(o1.distanceTo(this.position), o2.distanceTo(this.position));
    }

    public void setPos(double x, double y, double z) {
        this.position.set(x, y, z);
    }
}
