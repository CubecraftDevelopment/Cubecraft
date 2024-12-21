package net.cubecraft.client.internal.net;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.data.PacketChunkData;
import net.cubecraft.net.NetHandlerContext;
import net.cubecraft.net.packet.PacketListener;
import me.gb2022.commons.registry.TypeItem;

@TypeItem(NetHandlerType.CLIENT_DATA_RECEIVE)
public class ClientHandlerDataReceive extends ClientNetHandler {
    private final CubecraftClient client= CubecraftClient.getInstance();

    @PacketListener
    public void chunkDataReceived(PacketChunkData packet, NetHandlerContext context) {
        //todo
    }

}
