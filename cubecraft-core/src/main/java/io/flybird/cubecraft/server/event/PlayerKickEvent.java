package io.flybird.cubecraft.server.event;

import io.flybird.cubecraft.internal.entity.EntityPlayer;

public record PlayerKickEvent(EntityPlayer player, String reason){
}
