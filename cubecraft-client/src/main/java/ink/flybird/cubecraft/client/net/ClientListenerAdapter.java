package ink.flybird.cubecraft.client.net;

import io.flybird.cubecraft.net.NetworkListenerAdapter;
import io.flybird.cubecraft.net.packet.Packet;
import ink.flybird.fcommon.registry.ConstructingMap;

import java.net.InetSocketAddress;

public abstract class ClientListenerAdapter extends NetworkListenerAdapter {

    /**
     * an adapter of handler.
     *
     * @param packetConstructor packet registry.
     * @param handlerRegistry
     * @param attachEventBus
     */
    public ClientListenerAdapter(ConstructingMap<Packet> packetConstructor, ConstructingMap<ClientNetHandler> handlerRegistry) {
        super(packetConstructor);
        for (ClientNetHandler handler:handlerRegistry.createAll().values()){
            this.registerEventListener(handler);
        }
    }

    @Override
    public void disconnect(InetSocketAddress address) {
        this.disconnect();
    }

    @Override
    public void sendPacket(Packet pkt, InetSocketAddress address) {
        this.sendPacket(pkt);
    }

    public abstract void sendPacket(Packet pkt);

    public abstract void disconnect();
}