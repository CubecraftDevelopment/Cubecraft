package net.cubecraft.world.chunk;

import net.cubecraft.world.chunk.pos.ChunkPos;
import ink.flybird.fcommon.threading.SimpleThreadFactory;
import net.cubecraft.world.IWorld;
import net.cubecraft.level.Level;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ChunkProvider {
    protected final Level level;
    protected final ExecutorService threadPool;

    public ChunkProvider(Level level) {
        this.threadPool = Executors.newFixedThreadPool(1, new SimpleThreadFactory(this.getClass().getSimpleName(), true));
        this.level = level;
    }

    public abstract void generateChunk(IWorld world, ChunkPos pos);

    public void loadChunk(String worldID,ChunkPos... pos){
        for (ChunkPos p:pos){
            this.threadPool.submit(new Task(p,this,worldID));
        }
    }

    public Level getLevel() {
        return level;
    }

    record Task(ChunkPos pos, ChunkProvider provider, String worldID) implements Runnable {
        @Override
        public void run() {
            IWorld world = this.provider.getLevel().getDimension(this.worldID);
            this.provider.generateChunk(world, this.pos);
        }
    }
}