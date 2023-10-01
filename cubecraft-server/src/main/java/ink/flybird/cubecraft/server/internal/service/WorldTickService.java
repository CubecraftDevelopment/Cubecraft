package ink.flybird.cubecraft.server.internal.service;

import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.server.service.AbstractService;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.internal.thread.WorldTickingThread;
import ink.flybird.cubecraft.server.world.ServerWorld;
import ink.flybird.cubecraft.world.IWorld;
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
