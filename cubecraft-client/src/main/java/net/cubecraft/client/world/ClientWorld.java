package net.cubecraft.client.world;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//todo "bobby" styled chunk caching
public final class ClientWorld extends World {
    private static final int CLIENT_WORLD_SIMULATION_DISTANCE = 4;
    private final Executor chunkLoadExecutor;
    private final CubecraftClient client;

    public ClientWorld(String id, Level level, CubecraftClient client) {
        super(id, level);
        this.client = client;
        this.chunkLoadExecutor = null;//todo: configurable pool executor
    }


    @Override
    public WorldChunk getChunk(int cx, int cz, ChunkState state) {
        var ch = this.chunkCache.get(cx, cz);

        if (ch != null && ch.getState().isComplete(state)) {
            return ch;
        }

        return CompletableFuture.supplyAsync(() -> {
            var c = ((WorldChunk) null);//this.worldGenerator.load(cx, cz, state);//todo: network controller
            this.chunkCache.add(c);
            return c;
        }, this.chunkLoadExecutor).join();
    }

    @Override
    public WorldChunk threadSafeGetChunk(int cx, int cz, ChunkState state) {
        return this.getChunk(cx, cz, state);
    }


    @Override
    public void tick() {
        super.tick();
        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e == this.client.getWorldContext().getPlayer()) {
                e.tick();
                ChunkLoadAccess.loadChunkRange(
                        this,
                        ChunkPos.fromWorldPos((long) e.x, (long) e.z),
                        CLIENT_WORLD_SIMULATION_DISTANCE,
                        new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10)
                );
            } else {
                var cx = ChunkPos.x(e);
                var cz = ChunkPos.z(e);

                WorldChunk c = this.getChunk(cx, cz, ChunkState.COMPLETE);
                if (!c.task.shouldProcess(ChunkLoadTaskType.BLOCK_ENTITY_TICK)) {
                    it2.remove();
                }
            }
        }
        Iterator<WorldChunk> it = this.chunkCache.map().values().iterator();
        try {
            while (it.hasNext()) {
                WorldChunk chunk = it.next();
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
