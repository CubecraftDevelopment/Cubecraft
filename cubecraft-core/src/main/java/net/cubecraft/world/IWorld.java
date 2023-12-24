package net.cubecraft.world;

import ink.flybird.fcommon.container.KeyMap;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.hitting.Hittable;
import net.cubecraft.ContentRegistries;
import net.cubecraft.level.Level;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkLoader;
import net.cubecraft.world.chunk.ChunkSaver;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.future.ChunkFuture;
import net.cubecraft.world.chunk.future.CompletedFutureChunkContainer;
import net.cubecraft.world.chunk.future.FutureChunkContainer;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.dimension.Dimension;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.EntityMap;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.Predicate;

//todo:world access
public abstract class IWorld {
    public final HashMap<Vector3<Long>, Integer> scheduledTickEvents = new HashMap<>();
    public final KeyMap<ChunkPos, WorldChunk> chunks = new KeyMap<>();
    public final HashMap<String, Entity> entities = new HashMap<>();
    protected final EntityMap entityMap = new EntityMap(this);
    protected final Dimension dimension;
    protected final EventBus eventBus = new SimpleEventBus();
    protected final Level level;
    protected final String id;
    protected long time;

    protected ChunkSaver chunkSaver = null;
    protected ChunkLoader chunkLoader = null;

    public IWorld(String id, Level level) {
        this.id = id;
        this.level = level;
        this.dimension = ContentRegistries.DIMENSION.get(this.id);
    }

    public ChunkLoader getChunkLoader() {
        return chunkLoader;
    }

    public void setChunkLoader(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
    }

    public ChunkSaver getChunkSaver() {
        return chunkSaver;
    }

    public void setChunkSaver(ChunkSaver chunkSaver) {
        this.chunkSaver = chunkSaver;
    }

    public EntityMap getEntityMap() {
        return entityMap;
    }

    public HashMap<String, Entity> getEntities() {
        return entities;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ArrayList<AABB> getCollisionBoxInbound(AABB box) {
        ArrayList<AABB> result = new ArrayList<>();

        List<IBlockAccess> blocks = this.getBlockInRange(box);

        for (IBlockAccess access : blocks) {
            result.addAll(access.getCollisionBox());
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
                        AABB aabb = e.getCollisionBox();
                        if (result.contains(aabb)) {
                            continue;
                        }
                        result.add(aabb);
                    }
                }
            }
        }

        result.remove(box);
        return result;
    }

    public List<IBlockAccess> getBlockInRange(AABB box) {
        ArrayList<IBlockAccess> result = new ArrayList<>();

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
            if (!(Math.abs(e.x - from.x) < entity.getReachDistance() + 1
                    && Math.abs(e.y - from.y) < entity.getReachDistance() + 1
                    && Math.abs(e.z - from.z) < entity.getReachDistance() + 1
                    && entity != e)) {
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

    public KeyMap<ChunkPos, WorldChunk> getChunkCache() {
        return this.chunks;
    }


    //entity
    public void spawnEntity(String id, double x, double y, double z) {
        Entity e = ContentRegistries.ENTITY.create(id, this);
        e.setPos(x, y, z);
        this.addEntity(e);
    }

    public void addEntity(Entity e) {
        e.setWorld(IWorld.this);
        this.entities.put(e.getUuid(), e);
        this.loadChunk(ChunkPos.create((long) e.x / Chunk.WIDTH, (long) (e.z / Chunk.WIDTH)), new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 256));
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
    public WorldChunk getChunk(ChunkPos p) {
        return this.chunks.get(p);
    }

    public WorldChunk getChunk(long cx, long cz) {
        return this.getChunk(ChunkPos.create(cx, cz));
    }

    public ChunkFuture loadChunk(ChunkPos p, ChunkLoadTicket ticket) {
        WorldChunk c = getChunk(p);
        if (c != null) {
            c.addTicket(ticket);
            return new CompletedFutureChunkContainer(c);
        }
        this.chunkLoader.load(this, p, ticket);
        return new FutureChunkContainer(this, p);
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
        this.chunks.forceAdd(chunk);
        chunk.setWorld(this);
    }

    public boolean isAllNearMatch(long x, long y, long z, Predicate<IBlockAccess> predicate) {
        IBlockAccess[] states = new IBlockAccess[]{
                getBlockAccess(x + 1, y, z),
                getBlockAccess(x - 1, y, z),
                getBlockAccess(x, y + 1, z),
                getBlockAccess(x, y - 1, z),
                getBlockAccess(x, y, z + 1),
                getBlockAccess(x, y, z - 1)
        };
        return Arrays.stream(states).allMatch(predicate);
    }

    public boolean isChunkLoaded(ChunkPos p) {
        return this.chunks.contains(p);
    }

    public List<IBlockAccess> getAllBlockInRange(long x0, long y0, long z0, long x1, long y1, long z1) {
        ArrayList<IBlockAccess> blocks = new ArrayList<>();
        for (long i = x0; i <= x1; i++) {
            for (long j = y0; j <= y1; j++) {
                for (long k = z0; k <= z1; k++) {
                    blocks.add(getBlockAccess(i, j, k));
                }
            }
        }
        return blocks;
    }

    //todo:light fetch->Light engine

    public Dimension getDimension() {
        return dimension;
    }

    public IBlockAccess getBlockAccess(long x, long y, long z) {
        WorldChunk c = getChunk(ChunkPos.fromWorldPos(x, z));
        if (c != null) {
            return new ChunkBlockAccess(this, x, y, z, c);
        }
        return new NonLoadedBlockAccess(this, x, y, z);
    }


    //lock
    public void addChunkLock(ChunkPos p, Object caller) {
        WorldChunk chunk = this.getChunk(p);
        if (chunk != null) {
            chunk.getDataLock().addLock(caller);
        }
    }

    public void removeChunkLock(ChunkPos p, Object caller) {
        WorldChunk chunk = this.getChunk(p);
        if (chunk != null) {
            chunk.getDataLock().removeLock(caller);
        }
    }


    public void waitUntilChunkExist(ChunkPos pos) {
        while (!this.chunks.contains(pos)) {
            Thread.yield();
        }
    }

    public Level getLevel() {
        return level;
    }

    public int getHeight() {
        return 512;
    }

    public boolean areaSolidAndNear(long x0, long y0, long z0, long x1, long y1, long z1) {
        return this.getAllBlockInRange(x0, y0, z0, x1, y1, z1).stream().allMatch(block -> block.getBlock() != null
                && BlockPropertyDispatcher.isSolid(block)
                && this.getAllBlockInRange(x0, y1 + 1, z0, x1, y1 + 1, z1).stream().allMatch(BlockPropertyDispatcher::isSolid)
                && this.getAllBlockInRange(x0, y0 - 1, z0, x1, y0 - 1, z1).stream().allMatch(BlockPropertyDispatcher::isSolid)
                && this.getAllBlockInRange(x0 - 1, y0, z0, x0 - 1, y1, z1).stream().allMatch(BlockPropertyDispatcher::isSolid)
                && this.getAllBlockInRange(x1 + 1, y0, z0, x1 + 1, y1, z1).stream().allMatch(BlockPropertyDispatcher::isSolid)
                && this.getAllBlockInRange(x0, y0, z0 - 1, x1, y1, z0 - 1).stream().allMatch(BlockPropertyDispatcher::isSolid)
                && this.getAllBlockInRange(x0, y0, z1 + 1, x1, y1, z1 + 1).stream().allMatch(BlockPropertyDispatcher::isSolid));
    }

    public IBlockAccess[] getBlockAndNeighbor(long x, long y, long z) {
        return new IBlockAccess[]{
                getBlockAccess(x, y, z),
                getBlockAccess(x - 1, y, z),
                getBlockAccess(x + 1, y, z),
                getBlockAccess(x, y - 1, z),
                getBlockAccess(x, y + 1, z),
                getBlockAccess(x, y, z - 1),
                getBlockAccess(x, y, z + 1)
        };
    }

    public IBlockAccess[] getBlockNeighbor(long x, long y, long z) {
        return new IBlockAccess[]{
                getBlockAccess(x - 1, y, z),
                getBlockAccess(x + 1, y, z),
                getBlockAccess(x, y - 1, z),
                getBlockAccess(x, y + 1, z),
                getBlockAccess(x, y, z - 1),
                getBlockAccess(x, y, z + 1)
        };
    }

    public String getId() {
        return this.id;
    }

    public void save() {
    }

    //todo
    public <T extends Entity> T spawnEntity(Class<T> clazz){
        return null;
    }
}