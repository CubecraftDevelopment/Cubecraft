package net.cubecraft.event.entity;

import net.cubecraft.world.entity.Entity;

public record EntityAttackEvent(Entity from, Entity target) {
}
