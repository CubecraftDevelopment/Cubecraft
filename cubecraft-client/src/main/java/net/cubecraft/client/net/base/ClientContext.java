package net.cubecraft.client.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public interface ClientContext {
    void send(ByteBuf message);

    void disconnect();

    NetworkClient getClient();

    default void sendPacket(Packet packet){
        this.getClient().sendPacket(packet);
    }

    default InetSocketAddress getServerAddress(){
        return this.getClient().getServerAddress();
    }
}
