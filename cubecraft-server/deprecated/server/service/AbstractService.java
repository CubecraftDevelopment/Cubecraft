package ink.flybird.cubecraft.server.service;

import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.fcommon.threading.Service;

public abstract class AbstractService implements Service {
    private final ink.flybird.cubecraft.server.CubecraftServer server;

    protected AbstractService(ink.flybird.cubecraft.server.CubecraftServer server) {
        this.server = server;
    }

    public CubecraftServer getServer() {
        return server;
    }

    public void serverTick(){
    }
}
