package ink.flybird.cubecraft.server.internal.thread;

import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.threading.LoopTickingThread;
import ink.flybird.cubecraft.register.SharedContext;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.world.ServerWorld;

public final class WorldTickingThread extends LoopTickingThread {
    private final Logger logger;
    private final ServerWorld world;

    public WorldTickingThread(ServerWorld world, CubecraftServer server) {
        this.world = world;
        this.logger= SharedContext.LOG_CONTEXT.createLogger("server_world_tick@"+world.getID());
    }

    @Override
    public void init() {
        this.world.tick();
    }

    @Override
    public void tick() {
        this.world.tick();
    }

    @Override
    public void onException(Exception exception) {
        this.logger.exception(exception);
    }

    @Override
    public void onError(Error error) {
        this.logger.error(error);
    }
}
