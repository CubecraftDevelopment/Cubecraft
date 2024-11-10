package net.cubecraft.world;

import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.ContentRegistries;
import net.cubecraft.level.Level;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkCache;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.future.ChunkFuture;
import net.cubecraft.world.chunk.future.CompletedFutureChunkContainer;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.EntityMap;
import net.cubecraft.world.environment.Environment;
import net.cubecraft.world.environment.Environments;
import net.cubecraft.world.worldGen.WorldGenerator;
import org.joml.Vector3d;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class World implements BlockAccessor {
    public final HashMap<Vector3<Long>, Integer> scheduledTickEvents = new HashMap<>();
    public final HashMap<String, Entity> entities = new HashMap<>();
    protected final ChunkCache<WorldChunk> chunkCache = new ChunkCache<>();
    protected final EntityMap entityMap = new EntityMap(this);
    protected final Environment environment;
    protected final SimpleEventBus eventBus = new SimpleEventBus();
    protected final Level level;
    protected final String id;
    protected final WorldGenerator worldGenerator;
    protected Thread ownerThread = Thread.currentThread();
    protected long time;

    public World(String id, Level level) {
        this.id = id;
        this.level = level;
        this.environment = Environments.REGISTRY.object(id);
        this.worldGenerator = new WorldGenerator(this);
    }

    public void setOwnerThread() {
        this.ownerThread = Thread.currentThread();
    }

    public EntityMap getEntityMap() {
        return entityMap;
    }

    public HashMap<String, Entity> getEntities() {
        return entities;
    }

    public SimpleEventBus getEventBus() {
        return eventBus;
    }

    public ArrayList<AABB> getCollisionBoxInbound(AABB box) {
        ArrayList<AABB> result = new ArrayList<>();

        List<BlockAccess> blocks = this.getBlockInRange(box);

        for (BlockAccess access : blocks) {
            result.addAll(BlockPropertyDispatcher.getCollisionBox(access));
        }

        long xx0 = (long) Math.floor(box.x0);
        long yy0 = (long) Math.floor(box.y0);
        long zz0 = (long) Math.floor(box.z0);
        long xx1 = (long) Math.ceil(box.x1);
        long yy1 = (long) Math.ceil(box.y1);
        long zz1 = (long) Math.ceil(box.z1);


        for (long i = xx0; i <= xx1; i++) {
            for (long j = yy0; j <= yy1; j++) {
                for (long k = zz0; k <= zz1; k++) {
                    for (Entity e : this.getEntityMap().getEntitiesByBlock(i, j, k)) {
                        if (e == null) {
                            continue;
                        }
                        AABB aabb = e.getCollisionBox();
                        if (result.contains(aabb)) {
                            continue;
                        }
                        result.add(aabb);
                    }
                }
            }
        }

        result.addAll(Arrays.asList(this.environment.getBounding(box)));

        result.remove(box);
        return result;
    }

    public List<BlockAccess> getBlockInRange(AABB box) {
        ArrayList<BlockAccess> result = new ArrayList<>();

        long xx0 = (long) Math.floor(box.x0);
        long yy0 = (long) Math.floor(box.y0);
        long zz0 = (long) Math.floor(box.z0);
        long xx1 = (long) Math.ceil(box.x1);
        long yy1 = (long) Math.ceil(box.y1);
        long zz1 = (long) Math.ceil(box.z1);

        for (long i = xx0; i <= xx1; i++) {
            for (long j = yy0; j <= yy1; j++) {
                for (long k = zz0; k <= zz1; k++) {
                    result.add(this.getBlockAccess(i, j, k));
                }
            }
        }

        return result;
    }

    public boolean isFree(Collection<AABB> collisionBox) {
        for (AABB aabb : collisionBox) {
            for (AABB aabb2 : getCollisionBoxInbound(aabb)) {
                if (aabb.intersects(aabb2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Collection<Hittable> getSelectionBox(EntityLiving entity, Vector3d from, Vector3d dest) {
        ArrayList<Hittable> result = new ArrayList<>();

        for (long x = (long) Math.min(from.x, dest.x) - 2; x < Math.max(from.x, dest.x) + 2; x++) {
            for (long y = (long) Math.min(from.y, dest.y) - 2; y < Math.max(from.y, dest.y + 2) + 2; y++) {
                for (long z = (long) Math.min(from.z, dest.z) - 2; z < Math.max(from.z, dest.z) + 2; z++) {
                    result.add(getBlockAccess(x, y, z));
                }
            }
        }

        for (Entity e : this.entities.values()) {
            if (!(Math.abs(e.x - from.x) < entity.getReachDistance() + 1 && Math.abs(e.y - from.y) < entity.getReachDistance() + 1 && Math.abs(
                    e.z - from.z) < entity.getReachDistance() + 1 && entity != e)) {
                continue;
            }
            result.add(e);
        }
        return result;
    }


    //info
    public long getSeed() {
        return 0;
    }

    public long getTime() {
        return this.time;
    }

    public ChunkCache<WorldChunk> getChunkCache() {
        return this.chunkCache;
    }


    //entity
    public void spawnEntity(String id, double x, double y, double z) {
        Entity e = ContentRegistries.ENTITY.create(id, this);
        e.setPos(x, y, z);
        this.addEntity(e);
    }

    public void addEntity(Entity e) {
        e.setWorld(World.this);
        this.entities.put(e.getUuid(), e);
        this.loadChunk(ChunkPos.create((long) e.x / Chunk.WIDTH, (long) (e.z / Chunk.WIDTH)),
                       new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 256)
        );
    }

    public Collection<Entity> getAllEntities() {
        return this.entities.values();
    }

    public Entity getEntity(String uid) {
        return this.entities.get(uid);
    }

    public void removeEntity(String uid) {
        this.entities.remove(uid);
    }

    public void removeEntity(Entity e) {
        this.entities.remove(e.getUuid());
    }


    //load chunk
    public WorldChunk getChunk(int cx, int cz, ChunkState state) {
        var ch = this.ownerThread != Thread.currentThread() ? this.chunkCache.get(cx, cz) : this.chunkCache.cachedGet(cx, cz);

        if (ch != null && ch.getState().isComplete(state)) {
            return ch;
        }

        if (this.ownerThread != Thread.currentThread()) {
            CompletableFuture.supplyAsync(() -> {
                var c = this.worldGenerator.load(cx, cz, state);
                this.chunkCache.add(c);
                return c;
            }).join();
        }
        var c = this.worldGenerator.load(cx, cz, state);
        this.chunkCache.add(c);

        return c;
    }


    public WorldChunk getChunk(ChunkPos p) {
        return getChunk(p.getX(), p.getZ(), ChunkState.COMPLETE);
    }

    public WorldChunk getChunkSafely(int cx, int cz) {
        return this.getChunk(cx, cz, ChunkState.COMPLETE);
    }


    public ChunkFuture loadChunk(ChunkPos p, ChunkLoadTicket ticket) {
        WorldChunk chunk = getChunkSafely(p.getX(), p.getZ());
        chunk.addTicket(ticket);
        return new CompletedFutureChunkContainer(chunk);
    }

    //schedule tick
    public void setTick(long x, long y, long z) {
    }

    public void setTickSchedule(long x, long y, long z, int time) {
    }


    //tick
    public void tick() {
        time++;
    }


    public void setChunk(WorldChunk chunk) {
        this.chunkCache.add(chunk);
        chunk.setWorld(this);
    }

    public boolean isAllNearMatch(long x, long y, long z, Predicate<BlockAccess> predicate) {
        BlockAccess[] states = new BlockAccess[]{getBlockAccess(x + 1, y, z), getBlockAccess(x - 1, y, z), getBlockAccess(x,
                                                                                                                          y + 1,
                                                                                                                          z
        ), getBlockAccess(x, y - 1, z), getBlockAccess(x, y, z + 1), getBlockAccess(x, y, z - 1)};
        return Arrays.stream(states).allMatch(predicate);
    }

    public boolean isChunkLoaded(ChunkPos p) {
        return this.chunkCache.contains(p);
    }

    public BlockAccess[] getAllBlockInRange(long x0, long y0, long z0, long x1, long y1, long z1) {
        BlockAccess[] result = new BlockAccess[(int) ((x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1))];
        int counter = 0;
        for (long i = x0; i <= x1; i++) {
            for (long j = y0; j <= y1; j++) {
                for (long k = z0; k <= z1; k++) {
                    result[counter] = getBlockAccess(i, j, k);
                    counter++;
                }
            }
        }
        return result;
    }

    //todo:light fetch->Light engine

    public Environment getEnvironment() {
        return environment;
    }

    //todo:hotspot
    @Override
    public IBlockAccess getBlockAccess(long x, long y, long z) {
        WorldChunk c = this.chunkCache.getByWorldPos(x, z);

        if (c != null) {
            return c.getBlockAccess(x, y, z);
        }
        return new NonLoadedBlockAccess(this, x, y, z);
    }


    public void waitUntilChunkExist(int x, int z) {
        while (!this.chunkCache.contains(x, z)) {
            Thread.onSpinWait();
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread.yield();
        }
    }

    public Level getLevel() {
        return level;
    }

    public int getHeight() {
        return 512;
    }

    public BlockAccess[] getBlockAndNeighbor(long x, long y, long z) {
        return new BlockAccess[]{getBlockAccess(x, y, z), getBlockAccess(x - 1, y, z), getBlockAccess(x + 1, y, z), getBlockAccess(x,
                                                                                                                                   y - 1,
                                                                                                                                   z
        ), getBlockAccess(x, y + 1, z), getBlockAccess(x, y, z - 1), getBlockAccess(x, y, z + 1)};
    }

    public BlockAccess[] getBlockNeighbor(long x, long y, long z) {
        return new BlockAccess[]{getBlockAccess(x - 1, y, z), getBlockAccess(x + 1, y, z), getBlockAccess(x, y - 1, z), getBlockAccess(x,
                                                                                                                                       y + 1,
                                                                                                                                       z
        ), getBlockAccess(x, y, z - 1), getBlockAccess(x, y, z + 1)};
    }

    public String getId() {
        return this.id;
    }

    public void save() {
    }

    public <T extends Entity> T spawnEntity(Class<T> clazz) {
        try {
            T entity = clazz.getDeclaredConstructor(World.class).newInstance(this);
            this.addEntity(entity);
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof World world)) {
            return false;
        }

        return world.getId().equals(this.getId());
    }


    @Override
    public int getBlockId(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockID(this, x, y, z);
        }

        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        return chunk.getBlockId((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public byte getBlockMetadata(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockMeta(this, x, y, z);
        }

        var chunk = this.chunkCache.getByWorldPos(x, z);
        return chunk.getBlockMeta((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public byte getBlockLight(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockLight(this, x, y, z);
        }

        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        return chunk.getBlockLight((int) (x & 15), (int) y, (int) (z & 15));
    }

    public WorldGenerator getWorldGenerator() {
        return this.worldGenerator;
    }

    public boolean isChunkLoaded(int cx, int cz) {
        return this.chunkCache.contains(cx, cz);
    }

    public ChunkFuture<WorldChunk> loadChunk(int cx, int cz, ChunkLoadTicket ticket) {
        WorldChunk chunk = getChunkSafely(cx, cz);
        chunk.addTicket(ticket);
        return new CompletedFutureChunkContainer(chunk);
    }
}