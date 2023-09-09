package io.flybird.cubecraft.world.event.entity;

import io.flybird.cubecraft.world.entity.Entity;

public record EntityInteractEntityEvent(Entity From, Entity to) {
}
