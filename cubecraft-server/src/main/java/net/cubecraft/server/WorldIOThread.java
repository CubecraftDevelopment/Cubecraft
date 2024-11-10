package net.cubecraft.server;

import me.gb2022.commons.threading.LoopTickingThread;
import me.gb2022.commons.timer.Timer;
import net.cubecraft.server.world.ServerWorld;

public final class WorldIOThread extends LoopTickingThread {
    private final ServerWorld world;

    public WorldIOThread(ServerWorld world) {
        this.world = world;
        this.timer=new Timer(20);
    }

    public Thread createThread(ServerWorld world) {
        Thread t = new Thread(new WorldIOThread(world));

        t.setName("WorldIOThread[" + world.getId() + "]");
        t.setDaemon(true);

        return t;
    }

    public void start(){
        Thread t = new Thread(this);

        t.setName("WorldIOThread[" + this.world.getId() + "]");
        t.setDaemon(true);

        t.start();
    }

    public void tick() {
        this.world.GCChunks();
    }

}
