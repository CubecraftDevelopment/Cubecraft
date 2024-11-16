package net.cubecraft.world.worldGen.structure;

import net.cubecraft.util.register.Registered;
import net.cubecraft.world.World;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;

public final class StructureGeneratingContainer {
    private final World world;
    private final long baseX;
    private final int baseY;
    private final long baseZ;

    public StructureGeneratingContainer(long baseX, long baseY, long baseZ, World world) {
        this.baseX = baseX;
        this.baseY = (int) baseY;
        this.baseZ = baseZ;
        this.world = world;
    }

    public WorldChunk getAffectedChunk(int rx, int rz) {
        long wx = rx + this.baseX;
        long wz = rz + this.baseZ;

        return this.world.threadSafeGetChunk((int) (wx >> 4), (int) (wz >> 4), ChunkState.STRUCTURE);
    }

    public long getBaseX() {
        return baseX;
    }

    public long getBaseZ() {
        return baseZ;
    }

    public int getBaseY() {
        return baseY;
    }

    public void setBlockId(int rx, int ry, int rz, int id) {
        int cx = (int) (rx + this.baseX) & 15;
        int cy = ry + this.baseY;
        int cz = (int) (rz + this.baseZ) & 15;

        getAffectedChunk(rx, rz).setBlockId(cx, cy, cz, id);
    }

    public void setBlock(int rx, int ry, int rz, Registered<Block> block) {
        setBlockId(rx, ry, rz, block.getId());
    }
}
