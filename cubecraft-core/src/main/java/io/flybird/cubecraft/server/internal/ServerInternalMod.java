package io.flybird.cubecraft.server.internal;

import io.flybird.cubecraft.event.mod.ServerSideInitializeEvent;
import io.flybird.cubecraft.extansion.CubecraftExtension;
import io.flybird.cubecraft.server.ServerRegistries;
import io.flybird.cubecraft.server.internal.network.ServerHandlerConnection;
import io.flybird.cubecraft.server.internal.network.ServerHandlerDataFetch;
import io.flybird.cubecraft.server.internal.network.ServerHandlerPing;
import io.flybird.cubecraft.server.internal.network.ServerHandlerWorldListener;
import ink.flybird.fcommon.event.EventHandler;

@CubecraftExtension
public class ServerInternalMod {
    @EventHandler
    public void registerNetworkHandler(ServerSideInitializeEvent e){
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerConnection.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerPing.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerWorldListener.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerDataFetch.class);
    }
}
