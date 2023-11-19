package net.cubecraft.event.entity;

import net.cubecraft.world.entity.Entity;

public record EntityChatEvent(Entity sender, String message) {
}
