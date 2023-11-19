package net.cubecraft.world.chunk;

import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.task.ChunkProcessTask;
import ink.flybird.fcommon.nbt.NBTDataIO;
import ink.flybird.fcommon.threading.ThreadLock;
import net.cubecraft.world.IWorld;

//todo:task|add dynamic chunk height
public final class WorldChunk extends Chunk implements NBTDataIO {
    public final ChunkProcessTask task = new ChunkProcessTask(this);
    private final ThreadLock dataLock = new ThreadLock();
    private IWorld world;

    public WorldChunk(IWorld world, ChunkPos p) {
        super(p);
        this.world = world;
        this.task.addTime(ChunkLoadTaskType.DATA_KEEP, 40);
    }

    public WorldChunk(IWorld world, ProviderChunk providerChunk) {
        this(world, providerChunk.getKey());
        System.arraycopy(providerChunk.blockIdSections, 0, this.blockIdSections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.blockFacingSections, 0, this.blockFacingSections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.blockMetaSections, 0, this.blockMetaSections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.biomeSections, 0, this.biomeSections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.temperatureSections, 0, this.temperatureSections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.humiditySections, 0, this.humiditySections, 0, Chunk.SECTION_SIZE);
        System.arraycopy(providerChunk.lightSections, 0, this.lightSections, 0, Chunk.SECTION_SIZE);
    }

    public void tick() {
        this.task.run();
    }

    public void addTicket(ChunkLoadTicket ticket) {
        ticket.addToTask(this.task);
    }


        /*
        NBTTagCompound blockEntities = tag.getCompoundTag("block_entities");
        for (int i = 0; i < ChunkPos.WIDTH; i++) {
            for (int j = 0; j < ChunkPos.HEIGHT; j++) {
                for (int k = 0; k < ChunkPos.WIDTH; k++) {
                    String getKey = "%d/%d/%d".formatted(i, j, k);
                    if (blockEntities.getCompoundTag(getKey) != null) {
                        BlockEntity be = new BlockEntity("", (byte) 0, (byte) 0);
                        be.setData(blockEntities.getCompoundTag(getKey));
                        this.blockEntities.put(getKey, be);
                    }
                }
            }
        }
         */

    public ThreadLock getDataLock() {
        return dataLock;
    }

    public IWorld getWorld() {
        return world;
    }

    public void setWorld(IWorld world) {
        this.world = world;
    }

    public ChunkProcessTask getTask() {
        return task;
    }
}