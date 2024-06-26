package net.cubecraft.server;

import net.cubecraft.server.net.ServerNetHandler;
import net.cubecraft.server.service.Service;
import me.gb2022.commons.registry.ConstructingMap;

public class ServerSharedContext {
    public static final ConstructingMap<Service> SERVICE=new ConstructingMap<>(Service.class);

    public static final ConstructingMap<ServerNetHandler> NET_HANDLER=new ConstructingMap<>(ServerNetHandler.class);

    public static CubecraftServer SERVER;
}
