package net.cubecraft.client.net.base;

import io.netty.buffer.ByteBuf;
import net.cubecraft.client.net.packet.ClientPacketHandler;
import net.cubecraft.client.net.packet.ClientPacketHandlerAdapter;
import net.cubecraft.net.packet.Packet;

import java.util.HashMap;

public final class ClientPacketMessageHandler implements ClientListener {
    private final HashMap<ClientPacketHandler<?>, ClientPacketHandlerAdapter<?>> handlers = new HashMap<>(32);

    @Override
    public void handleMessage(ByteBuf message, ClientContext context) {
        if (!Packet.isPacket(message)) {
            return;
        }
        Packet packet = Packet.decode(message);
        for (ClientPacketHandlerAdapter<?> handler : this.handlers.values()) {
            handler.handle(packet, context);
        }
    }

    public <T extends Packet> void addHandler(ClientPacketHandler<T> handler, Class<T> typeOfT) {
        if (this.handlers.containsKey(handler)) {
            return;
        }
        ClientPacketHandlerAdapter<T> handlerAdapter = new ClientPacketHandlerAdapter<>(handler, typeOfT);
        this.handlers.put(handler, handlerAdapter);
    }

    public <T extends Packet> void removeHandler(ClientPacketHandler<T> handler) {
        if (!this.handlers.containsKey(handler)) {
            return;
        }
        this.handlers.remove(handler);
    }
}
