package net.cubecraft.server.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public interface ServerContext {
    void send(ByteBuf message);

    void disconnect();

    NetworkServer getServer();

    default void sendPacket(InetSocketAddress target, Packet packet) {
        this.getServer().sendPacket(target, packet);
    }

    InetSocketAddress getClientAddress();
}
