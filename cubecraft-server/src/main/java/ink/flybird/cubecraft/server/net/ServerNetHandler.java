package ink.flybird.cubecraft.server.net;

import ink.flybird.cubecraft.net.INetHandler;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.ServerSharedContext;

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
