package ink.flybird.cubecraft.client.internal.net;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import io.flybird.cubecraft.internal.network.NetHandlerType;
import io.flybird.cubecraft.internal.network.packet.*;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.world.entity.Entity;
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
    public void onPacketBlockChanged(PacketBlockChange packet, NetHandlerContext ctx) {
        this.client.getClientWorld().setBlockState(packet.getState());
    }

    @PacketListener
    public void clientLocationUpdate(PacketEntityPosition loc, NetHandlerContext ctx) {
        Entity e = this.client.getClientWorld().getEntity(loc.getUuid());
        if (Objects.equals(loc.getUuid(), this.client.getPlayer().getUID())) {

        } else {
            if (e != null && Objects.equals(loc.getNewLoc().getDim(), this.client.getClientWorld().getID())) {
                e.setLocation(loc.getNewLoc(), CollectionUtil.wrap(this.client.getClientWorld().getID(), this.client.getClientWorld()));
            }
        }
    }
}
