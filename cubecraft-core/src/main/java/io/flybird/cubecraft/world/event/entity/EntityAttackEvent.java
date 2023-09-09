package io.flybird.cubecraft.world.event.entity;

import io.flybird.cubecraft.world.entity.Entity;

public record EntityAttackEvent(Entity from, Entity target) {
}
