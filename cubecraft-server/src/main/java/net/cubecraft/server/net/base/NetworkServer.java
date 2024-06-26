package net.cubecraft.server.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.net.packet.ServerPacketHandler;

import java.net.InetSocketAddress;

public abstract class NetworkServer {
    private final ServerPacketMessageHandler handler = new ServerPacketMessageHandler();

    public abstract void start(InetSocketAddress address);

    public abstract void stop();

    public abstract void send(InetSocketAddress address, ByteBuf message);

    public abstract void broadcast(ByteBuf message);

    public abstract void addListener(ServerListener listener);

    public abstract void removeListener(ServerListener listener);

    public abstract InetSocketAddress getServerAddress();

    public final ServerPacketMessageHandler getPacketHandler() {
        return this.handler;
    }

    public void sendPacket(InetSocketAddress address, Packet packet) {
        this.send(address, Packet.encode(packet));
    }

    public <T extends Packet> void addHandler(Class<T> typeOfT, ServerPacketHandler<T> handler) {
        this.handler.addHandler(handler, typeOfT);
    }

    public <T extends Packet> void removeHandler(ServerPacketHandler<T> handler) {
        this.handler.removeHandler(handler);
    }

    public void injectPacketProcessor(){
        this.addListener(this.handler);
    }

    public abstract void disconnect(InetSocketAddress address);
}
