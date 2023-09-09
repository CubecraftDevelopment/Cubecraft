package ink.flybird.cubecraft.client.internal.net;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.*;
import io.flybird.cubecraft.world.event.block.BlockChangeEvent;
import io.flybird.cubecraft.world.event.entity.EntityAttackEvent;
import io.flybird.cubecraft.world.event.entity.EntityMoveEvent;
import io.flybird.cubecraft.world.event.world.ChunkLoadEvent;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.event.EventHandler;

@TypeItem(NetHandlerType.CLIENT_WORLD_LISTENER)
public class ClientHandlerWorldListener extends ClientNetHandler {


    @EventHandler
    public void onPlayerAttack(EntityAttackEvent e) {
        this.sendPacket(new PacketAttack(e.from().getUID(), e.target().getUID()));
    }

    @EventHandler
    public void onBlockChanged(BlockChangeEvent e) {
        this.sendPacket(new PacketBlockChange(e.x(), e.y(), e.z(), CubecraftClient.CLIENT.getClientWorld().getID(), e.newBlockState()));
    }

    @EventHandler
    public void onClientWorldChunkLoad(ChunkLoadEvent e) {
        this.sendPacket(new PacketChunkGet(e.pos(), CubecraftClient.CLIENT.getClientWorld().getID()));
        this.sendPacket(new PacketChunkLoad(CubecraftClient.CLIENT.getClientWorld().getID(), e.pos(), e.ticket()));
    }

    @EventHandler
    public void onPlayerMove(EntityMoveEvent e) {
        this.sendPacket(new PacketEntityPosition(e.e().getUID(), e.oldLocation()));
    }
}
