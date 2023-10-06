package ink.flybird.cubecraft.server;

import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.cubecraft.server.service.Service;
import ink.flybird.fcommon.registry.ConstructingMap;

public class ServerSharedContext {
    public static final ConstructingMap<Service> SERVICE=new ConstructingMap<>(Service.class);

    public static final ConstructingMap<ServerNetHandler> NET_HANDLER=new ConstructingMap<>(ServerNetHandler.class);
    public static CubecraftServer SERVER;
}
