package net.cubecraft.client.registry;

import net.cubecraft.client.internal.net.ClientHandlerConnection;
import net.cubecraft.client.internal.net.ClientHandlerDataReceive;
import net.cubecraft.client.internal.net.ClientHandlerWorldListener;
import net.cubecraft.client.net.ClientNetHandler;
import me.gb2022.commons.registry.ItemRegisterFunc;
import me.gb2022.commons.registry.ConstructingMap;

public class ClientNetworkHandlerRegistry {
    @ItemRegisterFunc(ClientNetHandler.class)
    public void registerClientHandlers(ConstructingMap<ClientNetHandler> handlers){
        handlers.registerItem(ClientHandlerDataReceive.class);
        handlers.registerItem(ClientHandlerWorldListener.class);
    }
}
