package net.cubecraft.client.render;

import ink.flybird.quantum3d_legacy.Camera;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class LevelRenderContext {
    private final List<WorldRenderObject> objects = new LinkedList<>();
    private final WorldRenderObjectSorter sorter = new WorldRenderObjectSorter();
    private final RenderType renderType;
    private final Camera camera;

    public LevelRenderContext(RenderType renderType, Camera camera) {
        this.renderType = renderType;
        this.camera = camera;
    }


    public List<WorldRenderObject> getObjects() {
        return objects;
    }

    public void updateViewPosition(Vector3d vec) {
        this.sorter.setPos(vec.x, vec.y, vec.z);
    }

    public void sortObjects() {
        this.objects.sort(this.sorter);
    }

    public void addObject(WorldRenderObject o) {
        this.objects.add(o);
    }

    public void removeObject(WorldRenderObject o) {
        this.objects.remove(o);
    }

    public void renderObjects() {
        List<WorldRenderObject> list = new ArrayList<>(this.objects);
        for (WorldRenderObject obj : list) {
            obj.render(this);
        }
    }

    public void clearObjects() {
        this.objects.clear();
    }

    public RenderType getRenderType() {
        return this.renderType;
    }

    public boolean hasObject(WorldRenderObject object) {
        return this.objects.contains(object);
    }

    public Camera getCamera() {
        return camera;
    }
}
