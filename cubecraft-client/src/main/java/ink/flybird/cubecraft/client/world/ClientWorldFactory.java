package ink.flybird.cubecraft.client.world;

import ink.flybird.cubecraft.client.CubecraftClient;
import io.flybird.cubecraft.level.Level;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.WorldFactory;

public class ClientWorldFactory implements WorldFactory {
    @Override
    public IWorld create(String id, Level level) {
        return new ClientWorld(id, level, CubecraftClient.CLIENT);
    }
}
