package net.cubecraft.server.world;

import me.gb2022.commons.container.CollectionUtil;
import me.gb2022.commons.container.Vector3;
import net.cubecraft.event.chunk.ChunkUnloadEvent;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.level.Location;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.world.World;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;

import java.util.*;

public class ServerWorld extends World {
    private final CubecraftServer server;

    public ServerWorld(String id, Level level, CubecraftServer server) {
        super(id, level);
        this.server = server;
    }

    @Override
    public void setTick(long x, long y, long z) {
        setTickSchedule(x, y, z, -1);
    }

    @Override
    public void setTickSchedule(long x, long y, long z, int time) {
        this.scheduledTickEvents.put(new Vector3<>(x, y, z), time);
    }

    @Override
    public void tick() {
        super.tick();

        var worldSpawnPoint = this.level.getLocation(null);
        if (Objects.equals(worldSpawnPoint.getWorldId(), this.getId())) {
            ChunkLoadAccess.loadChunkRange(this, worldSpawnPoint.getChunkPos(), 12, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 20));
        }


        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e instanceof EntityPlayer) {
                ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos(e.x, e.z), 5, ChunkLoadTicket.ENTITY);
            } else {
                WorldChunk c = this.getChunk(ChunkPos.fromWorldPos(e.x, e.z));
                if (c.task.shouldProcess(ChunkLoadTaskType.ENTITY_TICK)) {
                    e.tick();
                } else {
                    it2.remove();
                }
            }
        }

        HashMap<Vector3<Long>, Integer> times = (HashMap<Vector3<Long>, Integer>) this.scheduledTickEvents.clone();
        this.scheduledTickEvents.clear();
        CollectionUtil.iterateMap(times, (key, item) -> {
            WorldChunk c = getChunk((int) (key.x() >> 4), (int) (key.z() >> 4));
            if (item > 0 || c == null || !c.task.shouldProcess(ChunkLoadTaskType.BLOCK_TICK)) {
                this.scheduledTickEvents.put(key, item - 1);
            } else {
                getBlock(key.x(), key.y(), key.z()).get().onBlockUpdate(getBlockAccess(key.x(), key.y(), key.z()));
                this.scheduledTickEvents.remove(key);
            }
        });




        Iterator<WorldChunk> it = this.chunkCache.values().iterator();
        try {
            while (it.hasNext()) {
                WorldChunk chunk = it.next();
                chunk.tick();
                if (chunk.getDataLock().isLocked()) {
                    continue;
                }
                if (chunk.getTask().shouldLoad()) {
                    continue;
                }
                ChunkPos p = chunk.getKey();
                this.getEventBus().callEvent(new ChunkUnloadEvent(chunk, chunk.getWorld(), p.getX(), p.getZ()));
                this.worldGenerator.unload(chunk);
                it.remove();
            }
        } catch (Exception ignored) {
        }
    }

    public CubecraftServer getServer() {
        return server;
    }

    public void save() {
        for (WorldChunk chunk : this.chunkCache.map().values()) {
            this.worldGenerator.unload(chunk);
        }

        this.chunkCache.map().clear();
    }
}

//todo:计划刻，方块更新调度
