package net.cubecraft.event.entity;

import net.cubecraft.world.entity.Entity;

public record EntityInteractEntityEvent(Entity From, Entity to) {
}
