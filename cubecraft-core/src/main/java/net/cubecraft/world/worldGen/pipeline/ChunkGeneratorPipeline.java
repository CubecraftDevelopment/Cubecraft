package net.cubecraft.world.worldGen.pipeline;

import me.gb2022.commons.registry.TypeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ChunkGeneratorPipeline {
    private final HashMap<String, TerrainGeneratorHandler> handlers = new HashMap<>();
    private final ArrayList<String> idList = new ArrayList<>();
    private final long seed;

    public ChunkGeneratorPipeline(long seed) {
        this.seed = seed;
    }

    public ChunkGeneratorPipeline addFirst(String id, TerrainGeneratorHandler handler) {
        this.idList.remove(id);
        this.idList.add(0, id);
        this.handlers.put(id, handler);
        return this;
    }

    public ChunkGeneratorPipeline addLast(String id, TerrainGeneratorHandler handler) {
        this.idList.remove(id);
        this.idList.add(id);
        this.handlers.put(id, handler);
        return this;
    }

    public ChunkGeneratorPipeline addBefore(String target, String id, TerrainGeneratorHandler handler) {
        if (!this.idList.contains(target)) {
            return this;
        }
        this.idList.remove(id);
        this.idList.add(this.idList.indexOf(target) - 1, id);
        this.handlers.put(id, handler);
        return this;
    }

    public ChunkGeneratorPipeline addAfter(String target, String id, TerrainGeneratorHandler handler) {
        if (!this.idList.contains(target)) {
            return this;
        }
        this.idList.remove(id);
        this.idList.add(this.idList.indexOf(target) + 1, id);
        this.handlers.put(id, handler);
        return this;
    }

    public ChunkGeneratorPipeline addFirst(TerrainGeneratorHandler handler) {
        return this.addFirst(handler.getClass().getAnnotation(TypeItem.class).value(), handler);
    }

    public ChunkGeneratorPipeline addLast(TerrainGeneratorHandler handler) {
        return this.addLast(handler.getClass().getAnnotation(TypeItem.class).value(), handler);
    }

    public ChunkGeneratorPipeline addBefore(String target, TerrainGeneratorHandler handler) {
        return this.addBefore(target, handler.getClass().getAnnotation(TypeItem.class).value(), handler);
    }

    public ChunkGeneratorPipeline addAfter(String target, TerrainGeneratorHandler handler) {
        return this.addAfter(target, handler.getClass().getAnnotation(TypeItem.class).value(), handler);
    }

    public ChunkGeneratorPipeline remove(String id) {
        this.idList.remove(id);
        this.handlers.remove(id);
        return this;
    }

    public List<TerrainGeneratorHandler> getHandlerList() {
        List<TerrainGeneratorHandler> handlers = new ArrayList<>();
        for (String s : this.idList) {
            handlers.add(this.handlers.get(s));
        }
        return handlers;
    }

    public long getSeed() {
        return this.seed;
    }
}
