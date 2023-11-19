package net.cubecraft.server.net;

import net.cubecraft.net.INetHandler;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerSharedContext;

import java.net.InetSocketAddress;

public abstract class ServerNetHandler implements INetHandler {
    protected final CubecraftServer server= ServerSharedContext.SERVER;

    protected final void sendPacket(InetSocketAddress address, Packet pkt){
        this.server.getServerIO().sendPacket(pkt, address);
    }

    protected final void closeConnection(InetSocketAddress address){
        this.server.getServerIO().closeConnection(address);
    }

    protected final void allCloseConnection() {
        this.server.getServerIO().allCloseConnection();
    }

    protected final void broadcastPacket(Packet pkt){
        this.server.getServerIO().broadcastPacket(pkt);
    }
}
