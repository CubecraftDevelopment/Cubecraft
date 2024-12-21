package net.cubecraft.client.net;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.net.INetHandler;
import net.cubecraft.net.packet.Packet;

public abstract class ClientNetHandler implements INetHandler {
    protected final void sendPacket(Packet pkt){
        CubecraftClient.getInstance().getClientIO().sendPacket(pkt);
    }
}


