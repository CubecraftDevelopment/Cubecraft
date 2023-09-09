package io.flybird.cubecraft.server.world;

import io.flybird.cubecraft.server.CubecraftServer;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.level.Level;
import io.flybird.cubecraft.world.WorldFactory;

public class ServerWorldFactory implements WorldFactory {
    private final CubecraftServer server;

    public ServerWorldFactory(CubecraftServer server) {
        this.server = server;
    }

    @Override
    public IWorld create(String id, Level level) {
        //todo:???
        System.out.println(server);
        return null;
    }
}
