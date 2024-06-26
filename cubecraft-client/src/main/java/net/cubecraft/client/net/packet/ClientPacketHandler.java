package net.cubecraft.client.net.packet;

import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.net.packet.Packet;

@FunctionalInterface
public interface ClientPacketHandler<P extends Packet>{
    void handle(P packet, ClientContext context);
}
