package net.cubecraft.server.net;

import net.cubecraft.server.internal.network.ServerHandlerConnection;
import net.cubecraft.server.internal.network.ServerHandlerDataFetch;
import me.gb2022.commons.registry.ItemRegisterFunc;
import me.gb2022.commons.registry.ConstructingMap;

public class ServerNetworkHandlerRegistry {
    @ItemRegisterFunc(ServerNetHandler.class)
    public void registerServerHandlers(ConstructingMap<ServerNetHandler> handlers){
        handlers.registerItem(ServerHandlerDataFetch.class);
        handlers.registerItem(ServerHandlerConnection.class);
    }
}
