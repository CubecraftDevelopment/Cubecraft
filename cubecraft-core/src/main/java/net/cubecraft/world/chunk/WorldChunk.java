package net.cubecraft.world.chunk;

import me.gb2022.commons.nbt.NBTDataIO;
import me.gb2022.commons.threading.ThreadLock;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.task.ChunkProcessTask;

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


    public IBlockAccess getBlockAccess(long x, long y, long z) {
        if (x >> 4 != this.x || z >> 4 != this.z) {
            return new NonLoadedBlockAccess(this.getWorld(), x, y, z);
        }
        //todo:hotspot
        return new ChunkBlockAccess(this.getWorld(), x, y, z, this);
    }

    public IBlockAccess[] getAllBlockInRange(long x0, long y0, long z0, long x1, long y1, long z1) {
        IBlockAccess[] result = new IBlockAccess[(int) ((x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1))];
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
}