package ink.flybird.cubecraft.server.net;

import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.server.net.ServerListenerAdapter;

import java.net.InetSocketAddress;

public interface ServerIO {
    ServerListenerAdapter getListener();

    void sendPacket(Packet pkt, InetSocketAddress address);

    void closeConnection(InetSocketAddress address);

    void broadcastPacket(Packet pkt);

    void allCloseConnection();

    void start(int port,int maxConnection);
}
