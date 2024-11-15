package net.cubecraft.client.internal.net;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.PacketEntityData;
import net.cubecraft.internal.network.packet.PacketEntityPosition;
import net.cubecraft.internal.network.packet.PacketFullChunkData;
import net.cubecraft.net.NetHandlerContext;
import net.cubecraft.net.packet.PacketListener;
import net.cubecraft.world.entity.Entity;
import me.gb2022.commons.container.CollectionUtil;
import me.gb2022.commons.registry.TypeItem;

import java.util.Objects;

@TypeItem(NetHandlerType.CLIENT_DATA_RECEIVE)
public class ClientHandlerDataReceive extends ClientNetHandler {
    private final CubecraftClient client= ClientSharedContext.getClient();

    @PacketListener
    public void chunkDataReceived(PacketFullChunkData packet, NetHandlerContext context) {
        //todo
    }

}
