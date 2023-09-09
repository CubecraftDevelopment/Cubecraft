package io.flybird.cubecraft.server.net;

import io.flybird.cubecraft.server.internal.network.ServerHandlerConnection;
import io.flybird.cubecraft.server.internal.network.ServerHandlerDataFetch;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class ServerNetworkHandlerRegistry {
    @ItemRegisterFunc(ServerNetHandler.class)
    public void registerServerHandlers(ConstructingMap<ServerNetHandler> handlers){
        handlers.registerItem(ServerHandlerDataFetch.class);
        handlers.registerItem(ServerHandlerConnection.class);
    }
}
