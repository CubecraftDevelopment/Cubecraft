package net.cubecraft.server.net.packet;

import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.net.base.ServerContext;

public final class ServerPacketHandlerAdapter<T extends Packet> {
    private final ServerPacketHandler<T> handler;
    private final Class<T> clazz;

    public ServerPacketHandlerAdapter(ServerPacketHandler<T> handler, Class<T> clazz) {
        this.handler = handler;
        this.clazz = clazz;
    }

    public void handle(Packet packet, ServerContext context) {
        if (!this.clazz.isInstance(packet)) {
            return;
        }
        this.handler.handle(this.clazz.cast(packet), context);
    }
}
