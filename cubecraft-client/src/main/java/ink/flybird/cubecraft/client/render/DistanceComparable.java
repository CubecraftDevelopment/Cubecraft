package ink.flybird.cubecraft.client.render;

import io.flybird.cubecraft.world.entity.Entity;

public interface DistanceComparable {
    double distanceTo(Entity e);
}
