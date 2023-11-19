package net.cubecraft.client.internal.net;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.PacketEntityData;
import net.cubecraft.internal.network.packet.PacketEntityPosition;
import net.cubecraft.internal.network.packet.PacketFullChunkData;
import net.cubecraft.net.NetHandlerContext;
import net.cubecraft.net.packet.PacketListener;
import net.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.registry.TypeItem;

import java.util.Objects;

@TypeItem(NetHandlerType.CLIENT_DATA_RECEIVE)
public class ClientHandlerDataReceive extends ClientNetHandler {
    private final CubecraftClient client= CubecraftClient.CLIENT;

    @PacketListener
    public void chunkDataReceived(PacketFullChunkData packet, NetHandlerContext context) {
        //todo
    }

    @PacketListener
    public void entityReceived(PacketEntityData packet, NetHandlerContext context) {
        this.client.getClientWorld().addEntity(packet.getEntity());
    }


    @PacketListener
    public void clientLocationUpdate(PacketEntityPosition loc, NetHandlerContext ctx) {
        Entity e = this.client.getClientWorld().getEntity(loc.getUuid());
        if (Objects.equals(loc.getUuid(), this.client.getPlayer().getUuid())) {

        } else {
            if (e != null && Objects.equals(loc.getNewLoc().getDim(), this.client.getClientWorld().getId())) {
                e.setLocation(loc.getNewLoc(), CollectionUtil.wrap(this.client.getClientWorld().getId(), this.client.getClientWorld()));
            }
        }
    }
}
