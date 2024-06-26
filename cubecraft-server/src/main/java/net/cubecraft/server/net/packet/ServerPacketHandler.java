package net.cubecraft.server.net.packet;

import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.net.base.ServerContext;

@FunctionalInterface
public interface ServerPacketHandler<P extends Packet>{
    void handle(P packet, ServerContext context);
}
