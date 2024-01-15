package net.cubecraft.client.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.level.Level;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.WorldFactory;

public class ClientWorldFactory implements WorldFactory {
    @Override
    public IWorld create(String id, Level level) {
        return new ClientWorld(id, level, ClientSharedContext.getClient());
    }
}
