package ink.flybird.cubecraft.world.event.entity;

import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.cubecraft.world.entity.EntityLocation;

public record EntityMoveEvent(
        Entity e,
        EntityLocation oldLocation,
        EntityLocation newLocation
) {}
