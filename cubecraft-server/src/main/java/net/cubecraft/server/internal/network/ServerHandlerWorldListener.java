package net.cubecraft.server.internal.network;

import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.sync.PacketBlockChange;
import net.cubecraft.internal.network.packet.sync.PacketEntityPosition;
import net.cubecraft.server.net.ServerNetHandler;
import net.cubecraft.event.block.BlockChangeEvent;
import net.cubecraft.event.entity.EntityMoveEvent;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.commons.event.EventHandler;

@TypeItem(NetHandlerType.SERVER_WORLD_LISTENER)
public class ServerHandlerWorldListener extends ServerNetHandler {


    @EventHandler
    public void onBlockChanged(BlockChangeEvent event) {
        this.broadcastPacket(new PacketBlockChange(event.x(), event.y(), event.z(), event.world().getId(), event.newBlockState()));
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent e) {
        this.broadcastPacket(new PacketEntityPosition(e.e().getUuid(),e.newLocation()));
    }
}
