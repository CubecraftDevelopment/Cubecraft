package ink.flybird.cubecraft.server.world;

import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.access.ChunkLoadAccess;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.cubecraft.world.entity.EntityLocation;
import ink.flybird.cubecraft.world.worldGen.ChunkProvider;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class ServerWorld extends IWorld {
    private final ink.flybird.cubecraft.server.CubecraftServer server;

    public ServerWorld(String id, Level level, CubecraftServer server) {
        super(id, level);
        this.server = server;
    }

    //load chunk
    @Override
    public void loadChunk(ChunkPos p, ChunkLoadTicket ticket) {
        if (getChunk(p) == null) {
            this.chunks.add(new ChunkProvider(ServerWorld.this).loadChunk(p));
        } else {
            getChunk(p).addTicket(ticket);
        }
    }

    //schedule tick
    @Override
    public void setTick(long x, long y, long z) {
        setTickSchedule(x, y, z, -1);
    }

    @Override
    public void setTickSchedule(long x, long y, long z, int time) {
        this.scheduledTickEvents.put(new Vector3<>(x, y, z), time);
    }

    //tick
    @Override
    public void tick() {
        super.tick();
        //keep spawn location
        EntityLocation loc = this.level.getSpawnPoint("__LOAD");
        if (Objects.equals(loc.getDim(), this.getID())) {
          //  ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos((long) loc.getX(), (long) loc.getZ()), 2, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 20));
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
                it.remove();
            }
        } catch (ConcurrentModificationException ignored) {
        }
        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e instanceof EntityPlayer) {
                //this.server.getSetting().getValueAsInt("server.world.simulation_distance", 3)
                ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos((long) e.x, (long) e.z), 4, new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10));
            } else {
                WorldChunk c = this.getChunk(ChunkPos.create((long) (e.x) / 16, (long) (e.z) / 16));
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
                getBlockState(key.x(), key.y(), key.z()).getBlock().onBlockUpdate(this, key.x(), key.y(), key.z());
                this.scheduledTickEvents.remove(key);
            }
        });
    }
}
