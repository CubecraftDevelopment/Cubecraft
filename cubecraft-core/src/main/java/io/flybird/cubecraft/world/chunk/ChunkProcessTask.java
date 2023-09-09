package io.flybird.cubecraft.world.chunk;

import ink.flybird.fcommon.file.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.flybird.cubecraft.world.block.BlockState;

import java.util.HashMap;
import java.util.Random;

public class ChunkProcessTask implements Runnable, NBTDataIO {
    private final HashMap<ChunkLoadTaskType, Integer> levels = new HashMap<>();
    private final WorldChunk chunk;


    public ChunkProcessTask(WorldChunk chunk) {
        this.chunk = chunk;
        this.levels.put(ChunkLoadTaskType.BLOCK_ENTITY_TICK, 0);
        this.levels.put(ChunkLoadTaskType.ENTITY_TICK, 0);//process in ServerWorld
        this.levels.put(ChunkLoadTaskType.BLOCK_RANDOM_TICK, 0);
        this.levels.put(ChunkLoadTaskType.BLOCK_TICK, 0);//process in ServerWorld
        this.levels.put(ChunkLoadTaskType.DATA_CLEANUP, 0);
        this.levels.put(ChunkLoadTaskType.DATA_KEEP, 0);
    }

    public ChunkProcessTask(WorldChunk chunk, NBTTagCompound tag){
        this(chunk);
        this.setData(tag);
    }

    @Override
    public void run() {
        if (this.shouldProcess(ChunkLoadTaskType.BLOCK_ENTITY_TICK)) {
            for (BlockState s : this.chunk.getBlockEntityList()) {
                s.tick(this.chunk.getWorld());
            }
        }
        if (this.shouldProcess(ChunkLoadTaskType.BLOCK_RANDOM_TICK)) {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {//rand tick speed qwq
                chunk.getBlockState(
                        rand.nextInt(16),
                        rand.nextInt(16),
                        rand.nextInt(16)
                ).randomTick(chunk.getWorld());
            }
        }
        if (this.shouldProcess(ChunkLoadTaskType.DATA_KEEP)) {
            Random random = new Random();
            this.chunk.compressSections(random.nextInt(Chunk.SECTION_SIZE));
        }
    }

    public boolean shouldProcess(ChunkLoadTaskType type) {
        boolean process = false;
        int time = this.levels.get(type);
        if (time > 0) {
            process = true;
            time--;
        }
        this.levels.put(type, time);

        return process;
    }

    public void addTime(ChunkLoadTaskType type, int time) {
        int time2 = this.levels.get(type);
        this.levels.put(type, Math.max(time, time2));
    }

    public boolean shouldLoad() {
        return this.levels.get(ChunkLoadTaskType.DATA_KEEP) > 0;
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("block_entity_tick",this.levels.get(ChunkLoadTaskType.BLOCK_ENTITY_TICK));
        tag.setInteger("entity_tick",this.levels.get(ChunkLoadTaskType.ENTITY_TICK));
        tag.setInteger("block_random_tick",this.levels.get(ChunkLoadTaskType.BLOCK_RANDOM_TICK));
        tag.setInteger("block_tick",this.levels.get(ChunkLoadTaskType.BLOCK_TICK));
        tag.setInteger("data_cleanup",this.levels.get(ChunkLoadTaskType.DATA_CLEANUP));
        tag.setInteger("data_keep",this.levels.get(ChunkLoadTaskType.DATA_KEEP));
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.levels.put(ChunkLoadTaskType.BLOCK_ENTITY_TICK,tag.getInteger("block_entity_tick"));
        this.levels.put(ChunkLoadTaskType.ENTITY_TICK,tag.getInteger("entity_tick"));
        this.levels.put(ChunkLoadTaskType.BLOCK_RANDOM_TICK,tag.getInteger("block_random_tick"));
        this.levels.put(ChunkLoadTaskType.BLOCK_TICK,tag.getInteger("block_tick"));
        this.levels.put(ChunkLoadTaskType.DATA_CLEANUP,tag.getInteger("data_cleanup"));
        this.levels.put(ChunkLoadTaskType.DATA_KEEP,tag.getInteger("data_keep"));
    }
}
