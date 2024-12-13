package net.cubecraft.world.chunk;

import me.gb2022.commons.container.keymap.KeyGetter;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.BlockState;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.storage.ByteDataSection;
import net.cubecraft.world.chunk.storage.registry.RegistryStorageContainer;

import java.util.Collection;
import java.util.HashMap;

public abstract class Chunk implements KeyGetter<ChunkPos> {
    public static final int HEIGHT = 512;
    public static final int WIDTH = 16;
    public static final int SECTION_SIZE = HEIGHT / WIDTH;

    public final RegistryStorageContainer<Block> blocks;
    public final RegistryStorageContainer<Biome> biomes;
    public final ByteDataSection[] blockMetaSections;
    public final ByteDataSection[] lightSections;
    protected final ByteDataSection[] blockFacingSections;
    protected final int[] heightMap = new int[256];

    protected final int x, z;
    protected HashMap<String, BlockState> blockEntities = new HashMap<>();

    public Chunk(int cx, int cz, RegistryStorageContainer<Block> blocks, RegistryStorageContainer<Biome> biomes, ByteDataSection[] blockMetaSections, ByteDataSection[] lightSections, ByteDataSection[] blockFacingSections) {
        this.x = cx;
        this.z = cz;

        this.blocks = blocks;
        this.biomes = biomes;
        this.blockFacingSections = blockFacingSections;
        this.blockMetaSections = blockMetaSections;
        this.lightSections = lightSections;
    }


    public Chunk(int cx, int cz) {
        this(
                cx, cz,
                new RegistryStorageContainer<>(Blocks.REGISTRY, SECTION_SIZE),
                new RegistryStorageContainer<>(Biome.BIOMES, SECTION_SIZE),
                new ByteDataSection[SECTION_SIZE],
                new ByteDataSection[SECTION_SIZE],
                new ByteDataSection[SECTION_SIZE]
        );

        for (int i = 0; i < SECTION_SIZE; i++) {
            this.blockFacingSections[i] = new ByteDataSection();
            this.blockMetaSections[i] = new ByteDataSection();
            this.lightSections[i] = new ByteDataSection();
        }
    }

    static void validChunkOperation(int x, int y, int z) {
        if (validChunkOperationConditionally(x, y, z)) {
            throw new IllegalArgumentException("Invalid chunk coordinates(%s/%s/%s)".formatted(x, y, z));
        }
    }

    static boolean validChunkOperationConditionally(int x, int y, int z) {
        return x < 0 || x >= Chunk.WIDTH || z < 0 || z >= Chunk.WIDTH || y < 0 || y >= Chunk.HEIGHT;
    }

    public Collection<BlockState> getBlockEntityList() {
        return this.blockEntities.values();
    }

    //block state
    public BlockState getBlockState(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return new BlockState(getBlockID(x, y, z), getBlockFacing(x, y, z).getNumID(), getBlockMeta(x, y, z)).setX(this.getKey()
                                                                                                                           .toWorldPosX(x))
                .setY(y)
                .setZ(this.getKey().toWorldPosZ(z));
    }

    public void setBlockState(BlockState state) {
        int x = this.getKey().getRelativePosX(state.getX());
        int z = this.getKey().getRelativePosZ(state.getZ());

        ChunkPos.checkChunkRelativePosition(x, (int) state.getY(), z);
        this.setBlockID(x, (int) state.getY(), z, state.getId());
        this.setBlockFacing(x, (int) state.getY(), z, state.getFacing());
        this.setBlockMeta(x, (int) state.getY(), z, state.getMeta());
    }

    public int getBiome(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.biomes.get(x, y, z);
    }

    public void setBiome(int x, int y, int z, int biome) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.biomes.set(x, y, z, biome);
    }

    public String getBlockID(int x, int y, int z) {
        return Blocks.REGISTRY.name(getBlockId(x, y, z));
    }

    public Block getBlock(int x, int y, int z) {
        if (validChunkOperationConditionally(x, y, z)) {
            return Blocks.AIR.get();
        }
        return Blocks.REGISTRY.object(getBlockId(x, y, z));
    }

    public EnumFacing getBlockFacing(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return EnumFacing.fromId(this.blockFacingSections[y / WIDTH].get(x, y * WIDTH, z));
    }

    public byte getBlockMeta(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.blockMetaSections[y / WIDTH].get(x, y * WIDTH, z);
    }

    public byte getBlockLight(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);

        var a = getHighestBlockAt(x, z);

        if (a <= y) {
            return 127;
        }

        if (y >= 128) {
            return 127;
        }
        if (y < 96) {
            return 0;
        }
        return (byte) ((y - 96) * 4);

        //return this.lightSections[y / WIDTH].get(x, y % WIDTH, z);
    }

    public void setBlockID(int x, int y, int z, String id) {
        validChunkOperation(x, y, z);
        this.blocks.set(x, y, z, id);
    }

    public void setBlockFacing(int x, int y, int z, EnumFacing facing) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.blockFacingSections[y / WIDTH].set(x, y % WIDTH, z, facing.getNumID());
    }

    public void setBlockMeta(int x, int y, int z, byte meta) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.blockMetaSections[y / WIDTH].set(x, y % WIDTH, z, meta);
    }

    public void setBlockLight(int x, int y, int z, byte light) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.lightSections[y / WIDTH].set(x, y, z, light);
    }

    @Override
    public ChunkPos getKey() {
        return ChunkPos.create(x, z);
    }

    public void compressSections(int i) {
        this.blockFacingSections[i].tryCompress();
        this.blockMetaSections[i].tryCompress();
        this.lightSections[i].tryCompress();
    }

    public void calcHeightMap() {
        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int z = 0; z < Chunk.WIDTH; z++) {
                this.updateHeightMap(x, z);
            }
        }
    }

    public void updateHeightMap(int x, int z) {
        for (int i = HEIGHT - 1; i >= 0; i--) {
            if (getBlockId(x, i, z) != Blocks.AIR.getId()) {
                this.heightMap[x * 16 + z] = i;
                return;
            }
        }
    }

    public int getHighestBlockAt(int x, int z) {
        return this.heightMap[x * 16 + z];
    }

    public int getBlockId(int x, int y, int z) {
        if (validChunkOperationConditionally(x, y, z)) {
            return Blocks.AIR.getId();
        }
        return this.blocks.get(x, y, z);
    }

    public void setBlockId(int x, int y, int z, int id, boolean silent) {
        if (validChunkOperationConditionally(x, y, z)) {
            return;
        }
        this.blocks.set(x, y, z, id);

        if (!silent) {
            updateHeightMap(x, z);
        }
    }

    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(x, y, z, id, true);
    }

    public void compress(int position) {
        this.blocks.compress(position);
        this.biomes.compress(position);
        this.lightSections[position].tryCompress();
        this.blockMetaSections[position].tryCompress();
    }

    public void compress() {
        for (int position = 0; position < SECTION_SIZE; position++) {
            this.compress(position);
        }
    }
}
