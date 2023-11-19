package net.cubecraft.client.render;

import org.joml.Vector3d;

public interface WorldRenderObject {
    void render(LevelRenderContext context);

    double distanceTo(Vector3d position);

    String toString();
}
