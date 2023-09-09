package io.flybird.cubecraft.server.internal.network;

import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.PacketBlockChange;
import io.flybird.cubecraft.internal.network.packet.PacketEntityPosition;
import io.flybird.cubecraft.server.net.ServerNetHandler;
import io.flybird.cubecraft.world.event.block.BlockChangeEvent;
import io.flybird.cubecraft.world.event.entity.EntityMoveEvent;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.event.EventHandler;

@TypeItem(NetHandlerType.SERVER_WORLD_LISTENER)
public class ServerHandlerWorldListener extends ServerNetHandler {


    @EventHandler
    public void onBlockChanged(BlockChangeEvent event) {
        this.broadcastPacket(new PacketBlockChange(event.x(), event.y(), event.z(), event.world().getID(), event.newBlockState()));
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent e) {
        this.broadcastPacket(new PacketEntityPosition(e.e().getUID(),e.newLocation()));
    }
}
