package net.cubecraft.world.block.access;

import net.cubecraft.CoreRegistries;
import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.World;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

public class ChunkBlockAccess extends IBlockAccess {
    private final WorldChunk chunk;
    private String cachedBlockId;

    public ChunkBlockAccess(World world, long x, long y, long z, WorldChunk chunk) {
        super(world, x, y, z);
        this.chunk = chunk;
    }


    public int getBlockId() {
        return this.world.getBlockId(this.x, this.y, this.z);
    }

    @Override
    public byte getBlockMeta() {
        return this.world.getBlockMetadata(this.x, this.y, this.z);
    }

    @Override
    public byte getBlockLight() {
        return this.world.getBlockLight(this.x, this.y, this.z);
    }



    @Override
    public String getBlockID() {
        return CoreRegistries.BLOCKS.name(getBlockId());
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
    public Biome getBiome() {
        return CoreRegistries.BIOMES.object(this.chunk.getBiome((int) (this.x & 15), (int) this.y, (int) (this.z & 15)));
    }

    @Override
    public void setBiome(Registered<Biome> biome, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), biome.getId());
    }
}
