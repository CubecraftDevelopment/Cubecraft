package net.cubecraft.world.block.access;

import net.cubecraft.util.register.Registered;
import net.cubecraft.world.World;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

public class ChunkBlockAccess extends IBlockAccess {
    private final WorldChunk chunk;

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
        return Biome.BIOMES.object(this.chunk.getBiome((int) (this.x & 15), (int) this.y, (int) (this.z & 15)));
    }

    @Override
    public void setBiome(Registered<Biome> biome, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), biome.getId());
    }

    @Override
    public void setBlockId(int id, boolean silent) {
        this.chunk.setBlockId((int) (this.x & 15), (int) this.y, (int) (this.z & 15), id, silent);
    }
}
