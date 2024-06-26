package net.cubecraft.server.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.net.packet.ServerPacketHandler;
import net.cubecraft.server.net.packet.ServerPacketHandlerAdapter;

import java.util.HashMap;

public final class ServerPacketMessageHandler implements ServerListener {
    private final HashMap<ServerPacketHandler<?>, ServerPacketHandlerAdapter<?>> handlers = new HashMap<>(32);

    @Override
    public void handleMessage(ByteBuf message, ServerContext context) {
        if (!Packet.isPacket(message)) {
            return;
        }
        Packet packet = Packet.decode(message);
        for (ServerPacketHandlerAdapter<?> handler : this.handlers.values()) {
            handler.handle(packet, context);
        }
    }

    public <T extends Packet> void addHandler(ServerPacketHandler<T> handler, Class<T> typeOfT) {
        if (this.handlers.containsKey(handler)) {
            return;
        }
        ServerPacketHandlerAdapter<T> handlerAdapter = new ServerPacketHandlerAdapter<>(handler, typeOfT);
        this.handlers.put(handler, handlerAdapter);
    }

    public <T extends Packet> void removeHandler(ServerPacketHandler<T> handler) {
        if (!this.handlers.containsKey(handler)) {
            return;
        }
        this.handlers.remove(handler);
    }
}
