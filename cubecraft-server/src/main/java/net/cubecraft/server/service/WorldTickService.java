package net.cubecraft.server.service;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.WorldIOThread;
import net.cubecraft.server.world.ServerWorld;
import net.cubecraft.util.thread.BlockableEventLoop;
import net.cubecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@TypeItem("cubecraft:world_tick")
public final class WorldTickService implements Service {
    private static final Logger LOGGER = LogManager.getLogger("WorldTickService");
    private WorldTickingThread[] threads;
    private WorldIOThread[] ioThreads;


    @Override
    public void postInitialize(CubecraftServer server) {
        this.threads = new WorldTickingThread[server.getLevel().getWorldCount()];
        this.ioThreads = new WorldIOThread[server.getLevel().getWorldCount()];

        World[] worlds = server.getLevel().getWorlds().values().toArray(new World[0]);
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i] = new WorldTickingThread((ServerWorld) worlds[i]);
            this.threads[i].start();
        }

        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.ioThreads[i] = new WorldIOThread((ServerWorld) worlds[i]);
            this.ioThreads[i].start();
        }

        LOGGER.info("started {} world tick threads, {} IO threads", threads.length, ioThreads.length);
    }

    @Override
    public void stop(CubecraftServer server) {
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i].stop();
            this.ioThreads[i].setRunning(false);
        }
    }


    public static final class WorldEventLoop extends BlockableEventLoop<Runnable> {
        protected WorldEventLoop(String name) {
            super(name);
        }

        @Override
        protected Runnable of(Runnable command) {
            return command;
        }

        @Override
        protected boolean shouldRun(Runnable command) {
            return true;
        }

        @Override
        protected Thread getOwnerThread() {
            return null;
        }


    }






    public static final class WorldTickingThread {
        private final Logger logger;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private final ServerWorld world;
        private ScheduledFuture<?> future;

        public WorldTickingThread(ServerWorld world) {
            this.world = world;
            this.logger = LogManager.getLogger("WorldTickingThread[%s]".formatted(world.getId()));
        }

        public void start() {
            this.future = this.scheduler.scheduleAtFixedRate(() -> {
                this.world.setOwnerThread();
                try {
                    this.world.tick();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
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
