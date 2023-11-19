package net.cubecraft.server.world;

import net.cubecraft.level.Level;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.WorldFactory;

public class ServerWorldFactory implements WorldFactory {
    private final CubecraftServer server;

    public ServerWorldFactory(CubecraftServer server) {
        this.server = server;
    }

    @Override
    public IWorld create(String id, Level level) {
        return new ServerWorld(id, level, this.server);
    }
}
