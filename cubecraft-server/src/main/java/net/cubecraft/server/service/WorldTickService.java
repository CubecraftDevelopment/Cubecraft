package net.cubecraft.server.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.world.ServerWorld;
import net.cubecraft.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@TypeItem("cubecraft:world_tick")
public final class WorldTickService implements Service {
    private static final Logger LOGGER = LogManager.getLogger("server_world_tick");
    private WorldTickingThread[] threads;


    @Override
    public void postInitialize(CubecraftServer server) {
        this.threads = new WorldTickingThread[server.getLevel().getWorldCount()];
        World[] worlds = server.getLevel().getWorlds().values().toArray(new World[0]);
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i] = new WorldTickingThread((ServerWorld) worlds[i]);
            this.threads[i].start();
        }
    }

    @Override
    public void stop(CubecraftServer server) {
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i].stop();
        }
    }


    public static final class WorldTickingThread {
        private static final Logger LOGGER = LogManager.getLogger("server-world-tick");
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private final ServerWorld world;
        private ScheduledFuture<?> future;

        public WorldTickingThread(ServerWorld world) {
            this.world = world;
        }

        public void start() {
            this.future = this.scheduler.scheduleAtFixedRate(this.world::tick, 0, 50, TimeUnit.MILLISECONDS);
        }

        public void stop() {
            if (this.future == null || this.future.isCancelled()) {
                return;
            }
            this.future.cancel(true); // Cancel the scheduled task
            while (!this.future.isDone()) {
                Thread.onSpinWait();
                Thread.yield();
            }
            this.scheduler.shutdownNow();
        }
    }
}
