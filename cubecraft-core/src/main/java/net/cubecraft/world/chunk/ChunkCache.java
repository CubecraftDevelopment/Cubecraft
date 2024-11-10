package net.cubecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.cubecraft.world.chunk.pos.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.annotation.concurrent.NotThreadSafe;

import java.util.concurrent.atomic.AtomicLong;

public final class ChunkCache<C extends Chunk> {
    public static final Logger LOGGER = LogManager.getLogger("ChunkCache");

    private final Long2ObjectMap<C> chunks = new Long2ObjectOpenHashMap<>(8192);
    private final AtomicLong lastKey = new AtomicLong(0);
    private C lastValue;

    public C getByWorldPos(long x, long z) {
        return this.chunks.get(ChunkPos.encodeWorldPos(x, z));
    }

    public C get(int x, int z) {
        var key = ChunkPos.encode(x, z);
        return this.chunks.get(key);
    }

    @NotThreadSafe
    public C cachedGet(int x, int z) {
        var key = ChunkPos.encode(x, z);

        if (this.lastKey.get() == key) {
            return this.lastValue;
        }

        this.lastKey.set(key);
        this.lastValue = this.chunks.get(key);

        return this.lastValue;
    }

    public boolean contains(int x, int z) {
        return this.chunks.containsKey(ChunkPos.encode(x, z));
    }

    public C add(C chunk) {
        var encoded = ChunkPos.encode(chunk.x, chunk.z);

        if (this.chunks.containsKey(encoded)) {
            //LOGGER.warn("duplicated chunk insertion: {}/{}={}", chunk.x, chunk.z, encoded);
            return chunk;
        }

        try {
            this.chunks.put(ChunkPos.encode(chunk.x, chunk.z), chunk);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("chunk insert failed: " + chunk.x + "/" + chunk.z + "=" + encoded);
        }
        return chunk;
    }

    public boolean contains(ChunkPos p) {
        return this.chunks.containsKey(p.pack());
    }

    public C get(ChunkPos p) {
        return this.chunks.get(p.pack());
    }

    public Iterable<C> values() {
        return this.chunks.values();
    }

    public Long2ObjectMap<C> map() {
        return this.chunks;
    }
}
