package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.worldGen.pipeline.cache.HeightMap;

import java.util.HashMap;

public final class ChunkContext {
    private final HashMap<String, HeightMap> heightMapStorage = new HashMap<>();

    public HeightMap getHeightMap(String id) {
        return this.heightMapStorage.get(id);
    }

    public HeightMap createHeightMap(String id) {
        HeightMap m = new HeightMap();
        this.heightMapStorage.put(id, m);
        return m;
    }
}
