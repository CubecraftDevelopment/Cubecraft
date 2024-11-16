package net.cubecraft.world;

import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.level.Level;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.chunk.ChunkCache;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.EntityMap;
import net.cubecraft.world.environment.Environment;
import net.cubecraft.world.environment.Environments;
import org.joml.Vector3d;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

//todo:light fetch->Light engine
public abstract class World implements BlockAccessor {
    public final HashMap<Vector3<Long>, Integer> scheduledTickEvents = new HashMap<>();
    public final HashMap<String, Entity> entities = new HashMap<>();
    protected final ChunkCache<WorldChunk> chunkCache = new ChunkCache<>();
    protected final EntityMap entityMap = new EntityMap(this);
    protected final Environment environment;
    protected final SimpleEventBus eventBus = new SimpleEventBus();
    protected final Level level;
    protected final String id;

    protected long time;

    public World(String id, Level level) {
        this.id = id;
        this.level = level;
        this.environment = Environments.REGISTRY.object(id);
    }


    //----[BlockAccessor]----
    @Override
    public final BlockAccess getBlockAccess(long x, long y, long z) {
        WorldChunk c = this.chunkCache.getByWorldPos(x, z);

        if (c != null) {
            return c.getBlockAccess(x, y, z);
        }
        return new NonLoadedBlockAccess(this, x, y, z);
    }

    @Override
    public final int getBlockId(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockID(this, x, y, z);
        }

        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        return chunk.getBlockId((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public final byte getBlockMetadata(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockMeta(this, x, y, z);
        }

        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        return chunk.getBlockMeta((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public final byte getBlockLight(long x, long y, long z) {
        if (ChunkPos.isWorldPosInvalid(x, y, z)) {
            return this.environment.getBlockLight(this, x, y, z);
        }

        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        return chunk.getBlockLight((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public final void setBlockId(long x, long y, long z, int i, boolean silent) {
        var chunk = getChunkSafely((int) (x >> 4), (int) (z >> 4));
        chunk.setBlockId((int) (x & 15), (int) y, (int) (z & 15), i, silent);
    }


    //----[Object]----
    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof World world)) {
            return false;
        }

        return world.getId().equals(this.getId());
    }

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public final String toString() {
        return "World{" + "id='" + id + '\'' + ", chunkCache=" + chunkCache.map().size() + '}';
    }


    //----[Chunk]----
    public abstract WorldChunk getChunk(int cx, int cz, ChunkState state);

    public abstract WorldChunk threadSafeGetChunk(int cx, int cz, ChunkState state);

    public final WorldChunk getChunkSafely(int cx, int cz) {
        var c = this.getChunk(cx, cz, ChunkState.COMPLETE);

        if (c == null) {
            getChunk(cx, cz, ChunkState.COMPLETE);
        }

        return c;
    }

    public final WorldChunk loadChunk(int cx, int cz, ChunkLoadTicket ticket) {
        WorldChunk chunk = getChunk(cx, cz, ChunkState.COMPLETE);
        chunk.addTicket(ticket);
        return chunk;
    }

    public final void setChunk(WorldChunk chunk) {
        this.chunkCache.add(chunk);
        chunk.setWorld(this);
    }

    public final ChunkCache<WorldChunk> getChunkCache() {
        return this.chunkCache;
    }


    //----[Entity]----
    public final <T extends Entity> T spawnEntity(Class<T> clazz, double x, double y, double z) {
        try {
            T entity = clazz.getDeclaredConstructor(World.class).newInstance(this);
            entity.setPos(x, y, z);
            this.addEntity(entity);
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public final void addEntity(Entity e) {
        e.setWorld(World.this);
        this.entities.put(e.getUuid(), e);
        this.loadChunk(ChunkPos.x(e), ChunkPos.z(e), ChunkLoadTicket.ENTITY);
    }

    public final Entity getEntity(String uid) {
        return this.entities.get(uid);
    }

    public final void removeEntity(String uid) {
        this.entities.remove(uid);
    }

    public final void removeEntity(Entity e) {
        this.entities.remove(e.getUuid());
    }

    public final EntityMap getEntityMap() {
        return entityMap;
    }

    public final HashMap<String, Entity> getEntities() {
        return entities;
    }


    //----[Attribute]----
    public final long getSeed() {
        return getLevel().getLevelInfo().getSeed();
    }

    public final long getTime() {
        return this.time;
    }

    public final Environment getEnvironment() {
        return environment;
    }

    public final Level getLevel() {
        return level;
    }

    public final int getHeight() {
        return 512;
    }

    public final String getId() {
        return this.id;
    }

    public final SimpleEventBus getEventBus() {
        return eventBus;
    }

    public boolean isClient() {
        return false;
    }


    //----[physic]----
    public final ArrayList<AABB> getCollisionBoxInbound(AABB box) {
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

    public final List<BlockAccess> getBlockInRange(AABB box) {
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

    public final boolean isFree(Collection<AABB> collisionBox) {
        for (AABB aabb : collisionBox) {
            for (AABB aabb2 : getCollisionBoxInbound(aabb)) {
                if (aabb.intersects(aabb2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final Collection<Hittable> getSelectionBox(EntityLiving entity, Vector3d from, Vector3d dest) {
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


    //inherit
    public void tick() {
        time++;
    }

    public void save() {
    }
}