package net.cubecraft.server.internal;

import net.cubecraft.event.mod.ServerSideInitializeEvent;
import net.cubecraft.extension.CubecraftMod;
import net.cubecraft.extension.ModSide;
import net.cubecraft.server.ServerSharedContext;
import net.cubecraft.server.event.ServerSetupEvent;
import net.cubecraft.server.internal.network.ServerHandlerConnection;
import net.cubecraft.server.internal.network.ServerHandlerDataFetch;
import net.cubecraft.server.internal.network.ServerHandlerWorldListener;
import net.cubecraft.server.service.ChunkServiceImpl;
import net.cubecraft.server.service.WorldTickService;
import ink.flybird.fcommon.event.EventHandler;


@CubecraftMod(side = ModSide.SERVER)
public class ServerInternalMod {
    @EventHandler
    public void registerNetworkHandler(ServerSideInitializeEvent e) {
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerConnection.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerWorldListener.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerDataFetch.class);
    }

    @EventHandler
    public void onServerSetup(ServerSetupEvent event) {
        ServerSharedContext.SERVICE.registerItem(WorldTickService.class);
        ServerSharedContext.SERVICE.registerItem(ChunkServiceImpl.class);
    }
}
