package ink.flybird.cubecraft.client.net;

import io.flybird.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public abstract class ClientIO {
    public abstract ClientListenerAdapter getListener();

    public void sendPacket(Packet pkt){
        getListener().sendPacket(pkt);
    }

    public abstract void closeConnection();

    public abstract void connect(InetSocketAddress addr);

    public abstract boolean isRunning();
}
