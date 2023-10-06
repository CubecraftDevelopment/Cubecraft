package ink.flybird.cubecraft.server.event;

import ink.flybird.cubecraft.server.CubecraftServer;

public class ServerSetupEvent extends ServerEvent{
    public ServerSetupEvent(CubecraftServer server) {
        super(server);
    }
}
