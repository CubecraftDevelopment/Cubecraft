package ink.flybird.cubecraft.world.event.entity;

import ink.flybird.cubecraft.world.entity.Entity;

public record EntityAttackEvent(Entity from, Entity target) {
}
