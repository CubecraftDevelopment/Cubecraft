package ink.flybird.cubecraft.server.event;

import ink.flybird.cubecraft.internal.entity.EntityPlayer;

public record PlayerKickEvent(EntityPlayer player, String reason){
}
