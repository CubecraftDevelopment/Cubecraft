package net.cubecraft.server.event;

import net.cubecraft.server.CubecraftServer;

public class ServerSetupEvent extends ServerEvent{
    public ServerSetupEvent(CubecraftServer server) {
        super(server);
    }
}
