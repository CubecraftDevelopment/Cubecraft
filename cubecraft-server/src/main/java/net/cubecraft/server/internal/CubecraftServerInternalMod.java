package net.cubecraft.server.internal;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.mod.CubecraftMod;
import net.cubecraft.server.ServerSharedContext;
import net.cubecraft.server.event.ServerSetupEvent;
import net.cubecraft.server.internal.network.ServerHandlerConnection;
import net.cubecraft.server.internal.network.ServerHandlerDataFetch;
import net.cubecraft.server.internal.network.ServerHandlerWorldListener;
import net.cubecraft.server.service.ChunkService;
import net.cubecraft.server.service.WorldTickService;


@CubecraftMod
public class CubecraftServerInternalMod {
    public static final ILogger LOGGER= LogManager.getLogger("ServerInternalMod");

    @EventHandler
    public static void registerNetworkHandler(ModConstructEvent event) {
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerConnection.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerWorldListener.class);
        ServerSharedContext.NET_HANDLER.registerItem(ServerHandlerDataFetch.class);

        ServerSharedContext.SERVICE.registerItem(WorldTickService.class);
        ServerSharedContext.SERVICE.registerItem(ChunkService.class);
    }

    @EventHandler
    public static void onServerSetup(ServerSetupEvent event) {
        LOGGER.info("detected server started :D");
    }
}
