package net.cubecraft.world.chunk;

import me.gb2022.commons.threading.ThreadLock;
import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTaskType;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.task.ChunkProcessTask;

import java.util.Random;

//todo:task|add dynamic chunk height
public final class WorldChunk extends Chunk {
    public final ChunkProcessTask task = new ChunkProcessTask(this);
    private final ThreadLock dataLock = new ThreadLock();
    private final Random random = new Random();
    private ChunkState state;
    private World world;

    public WorldChunk(World world, ChunkPos p) {
        super(p);
        this.world = world;
        this.task.addTime(ChunkLoadTaskType.DATA_KEEP, 40);
        this.state = ChunkState.EMPTY;
    }

    public WorldChunk(World world, PrimerChunk providerChunk) {
        super(
                providerChunk.getKey(),
                providerChunk.blocks,
                providerChunk.biomes,
                providerChunk.blockMetaSections,
                providerChunk.lightSections,
                providerChunk.blockFacingSections
        );
        this.world = world;
        this.state = ChunkState.TERRAIN;
    }

    public void tick() {
        this.task.run();

        if (this.random.nextInt(0, 100) > 90) {
            var position = this.random.nextInt(SECTION_SIZE);
            this.compress(position);
        }
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

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public ChunkProcessTask getTask() {
        return task;
    }


    public IBlockAccess getBlockAccess(long x, long y, long z) {
        if (x >> 4 != this.x || z >> 4 != this.z) {
            return new NonLoadedBlockAccess(this.getWorld(), x, y, z);
        }

        return new ChunkBlockAccess(this.getWorld(), x, y, z, this);
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

    public ChunkState getState() {
        return state;
    }

    public void setState(ChunkState state) {
        this.state = state;
    }

    public BlockAccess getBlockAccessRelative(int x, long y, int z) {
        return new ChunkBlockAccess(this.getWorld(), x + this.x * 16, y, z + this.z * 16, this);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id, boolean silent) {
        var prev = getBlockId(x, y, z);

        super.setBlockId(x, y, z, id, silent);

        if (silent) {
            return;
        }

        var wx = ChunkPos.toWorld(this.x, x);
        var wz = ChunkPos.toWorld(this.z, z);

        this.world.getEventBus().callEvent(new BlockIDChangedEvent(this.world, wx, y, wz, prev, id));
        for (BlockAccess blockAccess : this.world.getBlockNeighbor(wx, y, wz)) {
            blockAccess.getBlock().onBlockUpdate(blockAccess);
        }

        var b = this.getBlock(x, y, z);
        if (b == null) {
            return;
        }
        b.onBlockUpdate(getBlockAccess(x, y, z));
    }
}