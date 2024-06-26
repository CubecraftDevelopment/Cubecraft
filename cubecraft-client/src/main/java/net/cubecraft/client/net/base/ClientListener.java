package net.cubecraft.client.net.base;

import io.netty.buffer.ByteBuf;

public interface ClientListener {
    default void onConnect(ClientContext context) {
    }

    default void onDisconnect(ClientContext context) {
    }

    default void handleMessage(ByteBuf message, ClientContext context) {
    }

    default void handleException(Throwable exception, ClientContext context) {
    }
}
