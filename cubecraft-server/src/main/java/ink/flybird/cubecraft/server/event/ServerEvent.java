package ink.flybird.cubecraft.server.event;

import ink.flybird.cubecraft.server.CubecraftServer;

public class ServerEvent {
    private final CubecraftServer server;

    public ServerEvent(CubecraftServer server) {
        this.server = server;
    }

    public CubecraftServer getServer() {
        return server;
    }
}
