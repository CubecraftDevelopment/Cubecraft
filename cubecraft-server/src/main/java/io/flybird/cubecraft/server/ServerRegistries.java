package io.flybird.cubecraft.server;

import io.flybird.cubecraft.server.net.ServerNetHandler;
import io.flybird.cubecraft.server.service.AbstractService;
import ink.flybird.fcommon.registry.ConstructingMap;

public class ServerRegistries {
    public static final ConstructingMap<AbstractService> SERVICE=new ConstructingMap<>(AbstractService.class);



    public static final ConstructingMap<ServerNetHandler> NET_HANDLER=new ConstructingMap<>(ServerNetHandler.class);
    public static CubecraftServer SERVER;
}
