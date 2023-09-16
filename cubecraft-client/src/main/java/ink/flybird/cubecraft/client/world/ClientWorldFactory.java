package ink.flybird.cubecraft.client.world;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.WorldFactory;

public class ClientWorldFactory implements WorldFactory {
    @Override
    public IWorld create(String id, Level level) {
        return new ClientWorld(id, level, CubecraftClient.CLIENT);
    }
}
