package net.cubecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.cubecraft.world.chunk.pos.ChunkPos;

public final class ChunkCache<C extends Chunk> {
    private final Long2ObjectMap<C> chunks = new Long2ObjectOpenHashMap<>(8192);

    public C getByWorldPos(long x, long z) {
        return this.chunks.get(ChunkPos.encodeWorldPos(x, z));
    }

    public C get(int x, int z) {
        return this.chunks.get(ChunkPos.encode(x, z));
    }

    public boolean contains(int x, int z) {
        return this.chunks.containsKey(ChunkPos.encode(x, z));
    }

    public C add(C chunk) {
        if (this.chunks.containsKey(chunk.getKey().pack())) {
            return chunk;
        }
        try {
            this.chunks.put(ChunkPos.encode(chunk.x, chunk.z), chunk);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(chunk.x + "," + chunk.z + "=" + ChunkPos.encode(chunk.x, chunk.z));
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
