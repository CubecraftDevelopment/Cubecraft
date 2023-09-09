package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.internal.net.ClientHandlerConnection;
import ink.flybird.cubecraft.client.internal.net.ClientHandlerDataReceive;
import ink.flybird.cubecraft.client.internal.net.ClientHandlerWorldListener;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class ClientNetworkHandlerRegistry {
    @ItemRegisterFunc(ClientNetHandler.class)
    public void registerClientHandlers(ConstructingMap<ClientNetHandler> handlers){
        handlers.registerItem(ClientHandlerDataReceive.class);
        handlers.registerItem(ClientHandlerConnection.class);
        handlers.registerItem(ClientHandlerWorldListener.class);
    }
}
