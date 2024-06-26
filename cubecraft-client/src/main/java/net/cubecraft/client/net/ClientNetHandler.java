package net.cubecraft.client.net;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.net.INetHandler;
import net.cubecraft.net.packet.Packet;

public abstract class ClientNetHandler implements INetHandler {
    protected final void sendPacket(Packet pkt){
        ClientSharedContext.getClient().getClientIO().sendPacket(pkt);
    }
}


