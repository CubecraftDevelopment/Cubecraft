package net.cubecraft.client.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.client.net.packet.ClientPacketHandler;
import net.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public abstract class NetworkClient {
    private final ClientPacketMessageHandler handler = new ClientPacketMessageHandler();

    public abstract void connect(InetSocketAddress address);

    public abstract void disconnect();

    public abstract void send(ByteBuf message);

    public abstract void addListener(ClientListener listener);

    public abstract void removeListener(ClientListener listener);

    public abstract InetSocketAddress getServerAddress();

    public final ClientPacketMessageHandler getPacketHandler() {
        return this.handler;
    }

    public void sendPacket(Packet packet) {
        this.send(Packet.encode(packet));
    }

    public <T extends Packet> void addHandler(Class<T> typeOfT, ClientPacketHandler<T> handler) {
        this.handler.addHandler(handler,typeOfT);
    }

    public <T extends Packet> void removeHandler(ClientPacketHandler<T> handler) {
        this.handler.removeHandler(handler);
    }

    public void injectPacketProcessor(){
        this.addListener(this.handler);
    }
}
