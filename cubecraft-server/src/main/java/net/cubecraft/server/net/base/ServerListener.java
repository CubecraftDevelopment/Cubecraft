package net.cubecraft.server.net.base;

import io.netty.buffer.ByteBuf;

public interface ServerListener {
    default void onDisconnect(ServerContext context) {
    }

    default void handleMessage(ByteBuf message, ServerContext context) {
    }

    default void handleException(Throwable exception, ServerContext context) {
    }
}
