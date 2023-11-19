package ink.flybird.cubecraft.server.internal.network;

import ink.flybird.cubecraft.internal.network.NetHandlerType;
import ink.flybird.cubecraft.internal.network.packet.PacketBlockChange;
import ink.flybird.cubecraft.internal.network.packet.PacketEntityPosition;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.cubecraft.event.block.BlockChangeEvent;
import ink.flybird.cubecraft.event.entity.EntityMoveEvent;
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
