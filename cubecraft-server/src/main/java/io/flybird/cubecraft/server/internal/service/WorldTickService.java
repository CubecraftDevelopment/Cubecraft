package io.flybird.cubecraft.server.internal.service;

import io.flybird.cubecraft.level.Level;
import io.flybird.cubecraft.register.SharedContext;
import io.flybird.cubecraft.server.service.AbstractService;
import io.flybird.cubecraft.server.CubecraftServer;
import io.flybird.cubecraft.server.internal.thread.WorldTickingThread;
import io.flybird.cubecraft.server.world.ServerWorld;
import io.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:world_tick")
public final class WorldTickService extends AbstractService {
    private final Logger logger= SharedContext.LOG_CONTEXT.createLogger("world_tick_service");
    private final Level level;

    private final WorldTickingThread[] threads;

    public WorldTickService(Level level, CubecraftServer server){
        super(server);
        this.level = level;
        this.threads=new WorldTickingThread[level.getWorldCount()];
    }

    @Override
    public void start() {
        IWorld[] worlds=this.level.getDims().values().toArray(new IWorld[0]);
        for (int i=0;i<level.getWorldCount();i++){
            this.threads[i]=new WorldTickingThread((ServerWorld) worlds[i],this.getServer());
            SharedContext.THREAD_INITIALIZER.makeThread("server_world_tick_"+i,this.threads[i]).start();
        }
    }

    @Override
    public void stop() {
        for (int i=0;i<level.getWorldCount();i++){
            this.threads[i].setRunning(false);
        }
    }
}
