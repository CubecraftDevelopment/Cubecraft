package ink.flybird.cubecraft.server.world;

import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.world.WorldFactory;

public class ServerWorldFactory implements WorldFactory {
    private final CubecraftServer server;

    public ServerWorldFactory(CubecraftServer server) {
        this.server = server;
    }

    @Override
    public IWorld create(String id, Level level) {
        //todo:???

        return null;
    }
}
