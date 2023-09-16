package ink.flybird.cubecraft.server.net;

import ink.flybird.cubecraft.server.internal.network.ServerHandlerConnection;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerDataFetch;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class ServerNetworkHandlerRegistry {
    @ItemRegisterFunc(ink.flybird.cubecraft.server.net.ServerNetHandler.class)
    public void registerServerHandlers(ConstructingMap<ServerNetHandler> handlers){
        handlers.registerItem(ServerHandlerDataFetch.class);
        handlers.registerItem(ServerHandlerConnection.class);
    }
}
