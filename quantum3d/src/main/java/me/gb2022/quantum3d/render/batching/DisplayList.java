package me.gb2022.quantum3d.render.batching;

import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public final class DisplayList {
    public static final int CAPACITY = 2048;

    private final RenderContext context;
    private final HashMap<String, RenderObject> mapping = new HashMap<>(CAPACITY);
    private final List<RenderObject> drawList = new ArrayList<>(CAPACITY);
    private final Vector3d viewPosition = new Vector3d();

    public DisplayList(RenderContext context) {
        this.context = context;
    }

    public void addObject(RenderObject object) {
        this.mapping.put(object.getId(), object);
    }

    public void removeObject(RenderObject object) {
        this.mapping.remove(object.getId(), object);
        this.drawList.remove(object);
    }

    public void updateVisibility(Predicate<RenderObject> predicate) {
        for (RenderObject object : this.mapping.values()) {
            if (predicate.test(object)) {
                if (this.drawList.contains(object)) {
                    continue;
                }
                this.drawList.add(object);
            } else {
                this.drawList.remove(object);
            }
        }
    }

    public void sort(Comparator<RenderObject> comparator) {
        this.drawList.sort(comparator);
    }

    public void render() {
        for (RenderObject object : this.drawList) {
            object.render(this.context);
        }
    }

    public void clear() {
        this.mapping.clear();
        this.drawList.clear();
    }
}
