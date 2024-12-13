package net.cubecraft.client.internal.net;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.client.net.packet.ClientPacketHandler;
import net.cubecraft.internal.network.NetHandlerType;
import net.cubecraft.internal.network.packet.PacketChunkGet;

@TypeItem(NetHandlerType.CLIENT_CONNECTION)
public class ClientHandlerConnection implements ClientPacketHandler<PacketChunkGet> {

    @Override
    public void handle(PacketChunkGet packet, ClientContext context) {

    }
}
