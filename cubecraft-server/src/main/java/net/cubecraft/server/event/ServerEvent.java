package net.cubecraft.server.event;

import net.cubecraft.server.CubecraftServer;

public class ServerEvent {
    private final CubecraftServer server;

    public ServerEvent(CubecraftServer server) {
        this.server = server;
    }

    public CubecraftServer getServer() {
        return server;
    }
}
