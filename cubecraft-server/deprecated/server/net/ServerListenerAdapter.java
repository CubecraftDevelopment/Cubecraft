package ink.flybird.cubecraft.server.net;

import ink.flybird.cubecraft.net.NetworkListenerAdapter;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.fcommon.registry.ConstructingMap;

public abstract class ServerListenerAdapter extends NetworkListenerAdapter {

    /**
     * an adapter of handler.
     *
     * @param packetConstructor packet registry.
     * @param handlerRegistry handler registry.
     */
    public ServerListenerAdapter(ConstructingMap<Packet> packetConstructor, ConstructingMap<ServerNetHandler> handlerRegistry) {
        super(packetConstructor);
        for (ServerNetHandler handler:handlerRegistry.createAll().values()){
            this.registerEventListener(handler);
        }
    }

    public abstract void broadCastPacket(Packet pkt);

    public abstract void allCloseConnection();
}
