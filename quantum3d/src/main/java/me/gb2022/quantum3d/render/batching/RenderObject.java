package me.gb2022.quantum3d.render.batching;

import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Vector3d;

public interface RenderObject {
    void render(RenderContext context);

    double distanceTo(Vector3d pos);

    String getId();
}
