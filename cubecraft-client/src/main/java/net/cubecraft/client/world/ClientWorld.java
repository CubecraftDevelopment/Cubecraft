package net.cubecraft.client.world;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.chunk.WorldChunk;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ClientWorld extends World {
    private static final int CLIENT_WORLD_SIMULATION_DISTANCE = 4;
    private final CubecraftClient client;

    public ClientWorld(String id, Level level, CubecraftClient client) {
        super(id, level);
        this.client = client;
    }



    @Override
    public void tick() {
        super.tick();
        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e == this.client.getClientWorldContext().getPlayer()) {
                e.tick();
                ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos((long) e.x, (long) e.z), CLIENT_WORLD_SIMULATION_DISTANCE, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10));
            } else {
                WorldChunk c = this.getChunk(ChunkPos.fromWorldPos((long) e.x, (long) e.z));
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
}
