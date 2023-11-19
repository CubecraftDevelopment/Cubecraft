package net.cubecraft.server.service;

import net.cubecraft.SharedContext;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.world.ServerWorld;
import net.cubecraft.world.IWorld;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.threading.LoopTickingThread;

import java.util.Arrays;

@TypeItem("cubecraft:world_tick")
public final class WorldTickService implements Service {
    private static final ILogger LOGGER = LogManager.getLogger("server_world_tick");
    private WorldTickingThread[] threads;

    @Override
    public void postInitialize(CubecraftServer server){
        this.threads = new WorldTickingThread[server.getLevel().getWorldCount()];
        IWorld[] worlds = server.getLevel().getWorlds().values().toArray(new IWorld[0]);
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i] = new WorldTickingThread((ServerWorld) worlds[i]);
            SharedContext.THREAD_INITIALIZER.makeThread("server_world_tick_" + i, this.threads[i]).start();
        }
    }

    @Override
    public void stop(CubecraftServer server) {
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i].setRunning(false);
        }
        while (Arrays.stream(this.threads).anyMatch(LoopTickingThread::isRunning)) {
            Thread.yield();
        }
    }

    public static final class WorldTickingThread extends LoopTickingThread {
        private static final ILogger LOGGER = LogManager.getLogger("server-world-tick");
        private final ServerWorld world;

        public WorldTickingThread(ServerWorld world) {
            this.world = world;
        }

        @Override
        public void init() {
            this.timer=new Timer(20.0f);
        }

        @Override
        public void tick() {
            this.world.tick();
        }

        @Override
        public boolean onException(Exception exception) {
            LOGGER.error(exception);
            return false;
        }

        @Override
        public boolean onError(Error error) {
            LOGGER.error(error);
            return false;
        }
    }
}
