package io.flybird.cubecraft.server.net;

import io.flybird.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public interface ServerIO {
    ServerListenerAdapter getListener();

    void sendPacket(Packet pkt, InetSocketAddress address);

    void closeConnection(InetSocketAddress address);

    void broadcastPacket(Packet pkt);

    void allCloseConnection();

    void start(int port,int maxConnection);
}
