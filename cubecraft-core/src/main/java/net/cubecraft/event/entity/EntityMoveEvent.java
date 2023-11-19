package net.cubecraft.event.entity;

import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.entity.EntityLocation;

public record EntityMoveEvent(
        Entity e,
        EntityLocation oldLocation,
        EntityLocation newLocation
) {}
