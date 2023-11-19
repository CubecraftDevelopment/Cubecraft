package net.cubecraft.client.registry;

import net.cubecraft.client.internal.net.ClientHandlerConnection;
import net.cubecraft.client.internal.net.ClientHandlerDataReceive;
import net.cubecraft.client.internal.net.ClientHandlerWorldListener;
import net.cubecraft.client.net.ClientNetHandler;
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
