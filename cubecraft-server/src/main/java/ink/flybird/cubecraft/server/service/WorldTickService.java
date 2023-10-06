package ink.flybird.cubecraft.server.service;

import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.world.ServerWorld;
import ink.flybird.cubecraft.world.IWorld;
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
    public void initialize(CubecraftServer server) {
        this.threads = new WorldTickingThread[server.getLevel().getWorldCount()];
        IWorld[] worlds = server.getLevel().getWorlds().values().toArray(new IWorld[0]);
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i] = new WorldTickingThread((ServerWorld) worlds[i], server);
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

        public WorldTickingThread(ServerWorld world, CubecraftServer server) {
            this.world = world;
        }

        @Override
        public void init() {
            this.timer=new Timer(20.0f);
            this.world.tick();
        }

        @Override
        public void tick() {
            this.world.tick();
        }

        @Override
        public void onException(Exception exception) {
            LOGGER.error(exception);
        }

        @Override
        public void onError(Error error) {
            LOGGER.error(error);
        }
    }
}
