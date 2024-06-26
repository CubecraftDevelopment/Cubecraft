package net.cubecraft.server.net;

import net.cubecraft.net.INetHandler;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerSharedContext;

import java.net.InetSocketAddress;

public abstract class ServerNetHandler implements INetHandler {
    protected final CubecraftServer server= ServerSharedContext.SERVER;

    protected final void sendPacket(InetSocketAddress address, Packet packet){
        this.server.getNetworkServer().sendPacket(address, packet);
    }

    protected final void closeConnection(InetSocketAddress address){
        this.server.getNetworkServer().disconnect(address);
    }

    protected final void allCloseConnection() {
        this.server.getNetworkServer().stop();
    }

    protected final void broadcastPacket(Packet pkt){
        this.server.getNetworkServer().stop();
    }
}
