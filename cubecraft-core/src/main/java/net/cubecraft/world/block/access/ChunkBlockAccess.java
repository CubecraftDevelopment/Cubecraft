package net.cubecraft.world.block.access;

import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.dimension.Dimension;

public class ChunkBlockAccess extends IBlockAccess {
    private final WorldChunk chunk;
    private String cachedBlockId;

    public ChunkBlockAccess(IWorld world, long x, long y, long z, WorldChunk chunk) {
        super(world, x, y, z);
        this.chunk = chunk;
    }

    @Override
    public String getBlockID() {
        if (this.cachedBlockId != null) {
            return this.cachedBlockId;
        }

        if (Dimension.outsideWorld(this.x, this.z) || Dimension.outsideWorldVertical(this.y)) {
            this.cachedBlockId = this.world.getDimension().predictBlockID(this.world, this.x, this.y, this.z);
            return this.cachedBlockId;
        }


        String id = this.chunk.getBlockID((int) (this.x & 15), (int) y, (int) (this.z & 15));
        if (id != null) {
            this.cachedBlockId = id;
        } else {
            this.cachedBlockId = this.world.getDimension().predictBlockID(this.world, this.x, this.y, this.z);
        }
        return this.cachedBlockId;
    }

    @Override
    public void setBlockID(String id, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }

        String old = getBlockID();

        this.chunk.setBlockID(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), id);
        if (!sendUpdateEvent) {
            return;
        }

        for (IBlockAccess blockAccess : this.world.getBlockNeighbor(this.x, this.y, this.z)) {
            blockAccess.getBlock().onBlockUpdate(blockAccess);
        }
        this.getBlock().onBlockUpdate(this);
        this.world.getEventBus().callEvent(new BlockIDChangedEvent(this.world, this.x, this.y, this.z, old, id));

    }

    @Override
    public EnumFacing getBlockFacing() {
        EnumFacing bs = this.world.getDimension().predictBlockFacingAt(this.world, this.x, this.y, this.z);
        if (bs != null) {
            return bs;
        } else {
            ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            return chunk.getBlockFacing(pos.getRelativePosX(this.x), (int) this.y, pos.getRelativePosZ(z));
        }
    }

    @Override
    public void setBlockFacing(EnumFacing facing, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockFacing(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), facing);
    }

    @Override
    public byte getBlockMeta() {
        Byte m = this.world.getDimension().predictBlockMetaAt(this.world, this.x, this.y, this.z);
        if (m != null) {
            return m;
        } else {
            ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            return this.chunk.getBlockMeta(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z));
        }
    }

    @Override
    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockMeta(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), meta);
    }

    @Override
    public byte getBlockLight() {
        Byte predictedLight = this.world.getDimension().predictLightAt(this.world, this.x, this.y, this.z);
        if (predictedLight != null) {
            return predictedLight;
        } else {
            if (this.y >= 128) {
                return (byte) (BlockPropertyDispatcher.isSolid(this) ? 0 : 127);
            }
            return (byte) (Math.max(128 - (128 - this.y) * 4, 8));

            //ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            //return this.chunk.getBlockLight(pos.getRelativePosX(this.x), (int) this.y, pos.getRelativePosZ(this.z));
        }
    }

    @Override
    public void setBlockLight(byte light, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockLight(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), light);
    }

    @Override
    public String getBiome() {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        return this.chunk.getBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z));
    }

    @Override
    public void setBiome(String biome, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), biome);
    }
}
