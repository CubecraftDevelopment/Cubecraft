package net.cubecraft.server.event.world;

import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.event.ServerEvent;
import net.cubecraft.server.world.ServerWorld;
import net.cubecraft.world.World;

public final class ServerWorldInitializedEvent extends ServerEvent {
    private final ServerWorld world;

    public ServerWorldInitializedEvent(CubecraftServer server, World world) {
        super(server);
        this.world = ((ServerWorld) world);
    }

    public ServerWorld getWorld() {
        return world;
    }
}
