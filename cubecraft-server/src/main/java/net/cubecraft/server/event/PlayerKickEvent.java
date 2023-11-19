package net.cubecraft.server.event;

import net.cubecraft.internal.entity.EntityPlayer;


public record PlayerKickEvent(EntityPlayer player, String reason) {
}
