package ink.flybird.cubecraft.server.internal;

import ink.flybird.cubecraft.event.mod.ServerSideInitializeEvent;
import ink.flybird.cubecraft.extension.CubecraftMod;
import ink.flybird.cubecraft.extension.ModSide;
import ink.flybird.cubecraft.server.ServerSharedContext;
import ink.flybird.cubecraft.server.event.ServerSetupEvent;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerConnection;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerDataFetch;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerPing;
import ink.flybird.cubecraft.server.internal.network.ServerHandlerWorldListener;
import ink.flybird.cubecraft.server.service.DBChunkService;
import ink.flybird.cubecraft.server.service.WorldTickService;
import ink.flybird.fcommon.event.EventHandler;


@CubecraftMod(side = ModSide.SERVER)
public class ServerInternalMod {
    @EventHandler
    public void registerNetworkHandler(ServerSideInitializeEvent e) {
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerConnection.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerPing.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerWorldListener.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerDataFetch.class);
    }

    @EventHandler
    public void onServerSetup(ServerSetupEvent event) {
        ServerSharedContext.SERVICE.registerItem(WorldTickService.class);
        ServerSharedContext.SERVICE.registerItem(DBChunkService.class);
    }
}
