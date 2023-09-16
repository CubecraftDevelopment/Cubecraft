package ink.flybird.cubecraft.server.service;

import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.fcommon.threading.Service;

public abstract class AbstractService implements Service {
    private final CubecraftServer server;

    protected AbstractService(CubecraftServer server) {
        this.server = server;
    }

    public CubecraftServer getServer() {
        return server;
    }

    public void serverTick(){
    }
}
