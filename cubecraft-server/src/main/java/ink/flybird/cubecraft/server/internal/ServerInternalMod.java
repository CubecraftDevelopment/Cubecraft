package ink.flybird.cubecraft.server.internal;

import ink.flybird.cubecraft.extansion.ExtensionSide;
import ink.flybird.cubecraft.mod.ServerSideInitializeEvent;
import ink.flybird.cubecraft.extansion.CubecraftExtension;
import ink.flybird.cubecraft.server.ServerRegistries;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerConnection;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerDataFetch;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerPing;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerWorldListener;
import ink.flybird.fcommon.event.EventHandler;


@CubecraftExtension(side = ExtensionSide.SERVER)
public class ServerInternalMod {
    @EventHandler
    public void registerNetworkHandler(ServerSideInitializeEvent e){
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerConnection.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerPing.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerWorldListener.class);
        ServerRegistries.NET_HANDLER.registerItem(ServerHandlerDataFetch.class);
    }
}
