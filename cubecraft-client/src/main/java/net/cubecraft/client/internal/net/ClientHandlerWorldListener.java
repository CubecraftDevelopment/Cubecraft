package net.cubecraft.client.internal.net;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.event.block.BlockChangeEvent;
import net.cubecraft.event.entity.EntityAttackEvent;
import net.cubecraft.event.entity.EntityMoveEvent;
import net.cubecraft.event.world.ChunkLoadEvent;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.commons.event.EventHandler;
import net.cubecraft.internal.network.packet.*;

@TypeItem(NetHandlerType.CLIENT_WORLD_LISTENER)
public class ClientHandlerWorldListener extends ClientNetHandler {


    @EventHandler
    public void onPlayerAttack(EntityAttackEvent e) {
        this.sendPacket(new PacketAttack(e.from().getUuid(), e.target().getUuid()));
    }


    @EventHandler
    public void onPlayerMove(EntityMoveEvent e) {
        this.sendPacket(new PacketEntityPosition(e.e().getUuid(), e.oldLocation()));
    }
}
