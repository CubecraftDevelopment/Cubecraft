package net.cubecraft.server.net;

import net.cubecraft.net.NetworkListenerAdapter;
import net.cubecraft.net.packet.Packet;
import me.gb2022.commons.registry.ConstructingMap;

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
