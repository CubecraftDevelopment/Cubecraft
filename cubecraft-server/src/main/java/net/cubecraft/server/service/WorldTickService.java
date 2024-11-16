package net.cubecraft.server.service;

import me.gb2022.commons.registry.TypeItem;
import me.gb2022.commons.threading.SimpleThreadFactory;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.world.ServerWorld;
import net.cubecraft.util.thread.EventLoop;
import net.cubecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

@TypeItem("cubecraft:world_tick")
public final class WorldTickService implements Service {
    private static final Logger LOGGER = LogManager.getLogger("WorldTickService");
    private WorldTickingThreadHolder[] threads;
    private EventLoop[] ioThreads;


    @Override
    public void postInitialize(CubecraftServer server) {
        this.threads = new WorldTickingThreadHolder[server.getLevel().getWorldCount()];
        this.ioThreads = new EventLoop[server.getLevel().getWorldCount()];

        World[] worlds = server.getLevel().getWorlds().values().toArray(new World[0]);
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            var world = ((ServerWorld) worlds[i]);

            var thread = new EventLoop(() -> {
                world.setOwnerThread();
                world.setChunkIOService(((Executor) Thread.currentThread()));
            });

            thread.setName("WorldIOThread[%s]#%d".formatted(i,server.hashCode()));
            thread.setDaemon(true);
            thread.start();

            this.ioThreads[i] = thread;

            this.threads[i] = new WorldTickingThreadHolder((ServerWorld) worlds[i]);
            this.threads[i].start();
        }

        LOGGER.info("started {} world tick threads, {} IO threads", threads.length, ioThreads.length);
    }

    @Override
    public void stop(CubecraftServer server) {
        for (int i = 0; i < server.getLevel().getWorldCount(); i++) {
            this.threads[i].stop();
            this.ioThreads[i].shutdown();
        }
    }


    public static final class WorldTickingThreadHolder {
        private final Logger logger;
        private final ServerWorld world;
        private final ScheduledExecutorService scheduler;
        private ScheduledFuture<?> future;

        public WorldTickingThreadHolder(ServerWorld world) {
            var tid = "WorldTickingThread[%s]#%d".formatted(world.getId(),world.getServer().hashCode());

            this.world = world;
            this.logger = LogManager.getLogger(tid);
            this.scheduler = Executors.newScheduledThreadPool(1, new SimpleThreadFactory(tid, true));
        }

        public void start() {
            this.future = this.scheduler.scheduleAtFixedRate(() -> {
                try {
                    this.world.tick();
                } catch (Throwable e) {
                    this.logger.catching(e);
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
