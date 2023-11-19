package net.cubecraft.server.world;

import net.cubecraft.event.chunk.ChunkUnloadEvent;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.level.Location;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.math.MathHelper;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class ServerWorld extends IWorld {
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
        Location worldSpawnPoint = this.level.getLocation(null);
        if (Objects.equals(worldSpawnPoint.getWorldId(), this.getId())) {
            ChunkLoadAccess.loadChunkRange(this, worldSpawnPoint.getChunkPos(), 4, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 20));
        }

        Iterator<WorldChunk> it = this.chunks.map.values().iterator();
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
                this.chunkSaver.save(chunk);
                it.remove();
            }
        } catch (ConcurrentModificationException ignored) {
        }


        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e instanceof EntityPlayer) {
                ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos((long) e.x, (long) e.z), 1, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10));
            } else {
                WorldChunk c = this.getChunk(ChunkPos.create((long) (e.x) / 16, (long) (e.z) / 16));
                if (c.task == null) {
                    continue;
                }
                if (c.task.shouldProcess(ChunkLoadTaskType.BLOCK_ENTITY_TICK)) {
                    e.tick();
                } else {
                    it2.remove();
                }
            }
        }
        HashMap<Vector3<Long>, Integer> times = (HashMap<Vector3<Long>, Integer>) this.scheduledTickEvents.clone();
        this.scheduledTickEvents.clear();
        CollectionUtil.iterateMap(times, (key, item) -> {
            WorldChunk c = getChunk(
                    MathHelper.getRelativePosInChunk(key.x(), Chunk.WIDTH),
                    MathHelper.getRelativePosInChunk(key.z(), Chunk.WIDTH));
            if (item > 0 || c == null || !c.task.shouldProcess(ChunkLoadTaskType.BLOCK_TICK)) {
                this.scheduledTickEvents.put(key, item - 1);
            } else {
                //getBlockState(key.x(), key.y(), key.z()).getBlock().onBlockUpdate(this, key.x(), key.y(), key.z());
                this.scheduledTickEvents.remove(key);
            }
        });
    }

    public CubecraftServer getServer() {
        return server;
    }

    public void save() {
        Iterator<WorldChunk> it = ((HashMap<?, WorldChunk>) this.chunks.map.clone()).values().iterator();
        while (it.hasNext()) {
            WorldChunk chunk = it.next();
            this.chunkSaver.save(chunk);
            it.remove();
        }
    }
}

//todo:计划刻，方块更新调度
