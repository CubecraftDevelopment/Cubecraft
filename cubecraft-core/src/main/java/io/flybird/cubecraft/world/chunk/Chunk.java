package io.flybird.cubecraft.world.chunk;

import ink.flybird.fcommon.container.KeyGetter;
import ink.flybird.fcommon.file.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.flybird.cubecraft.world.block.BlockState;
import io.flybird.cubecraft.world.block.EnumFacing;
import io.flybird.cubecraft.world.chunk.storage.*;

import java.util.Collection;
import java.util.HashMap;

//todo swap data to storage
public abstract class Chunk implements KeyGetter<ChunkPos>, NBTDataIO {
    public static final int HEIGHT = 512;
    public static final int WIDTH = 16;
    public static final int SECTION_SIZE = HEIGHT / WIDTH;

    protected final StringDataSection[] blockIdSections = new StringDataSection[SECTION_SIZE];
    protected final ByteDataSection[] blockFacingSections = new ByteDataSection[SECTION_SIZE];
    protected final ByteDataSection[] blockMetaSections = new ByteDataSection[SECTION_SIZE];
    protected final ByteDataSection[] lightSections = new ByteDataSection[SECTION_SIZE];
    protected final StringDataSection[] biomeSections = new StringDataSection[SECTION_SIZE];
    protected final ByteDataSection[] temperatureSections = new ByteDataSection[SECTION_SIZE];
    protected final ByteDataSection[] humiditySections = new ByteDataSection[SECTION_SIZE];


    protected final long x, z;
    protected HashMap<String, BlockState> blockEntities = new HashMap<>();

    public Chunk(ChunkPos p) {
        this.x = p.x();
        this.z = p.z();
        for (int i = 0; i < SECTION_SIZE; i++) {
            this.blockIdSections[i] = new StringDataSection();
            this.blockFacingSections[i] = new ByteDataSection();
            this.blockMetaSections[i] = new ByteDataSection();
            this.lightSections[i] = new ByteDataSection();
            this.biomeSections[i] = new StringDataSection();
            this.temperatureSections[i] = new ByteDataSection();
            this.humiditySections[i] = new ByteDataSection();
        }
    }


    public Collection<BlockState> getBlockEntityList() {
        return this.blockEntities.values();
    }


    //block state
    public BlockState getBlockState(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return new BlockState(getBlockID(x, y, z), getBlockFacing(x, y, z).getNumID(), getBlockMeta(x, y, z)).setX(this.getKey().toWorldPosX(x)).setY(y).setZ(this.getKey().toWorldPosZ(z));
    }

    public void setBlockState(BlockState state) {
        int x = this.getKey().getRelativePosX(state.getX());
        int z = this.getKey().getRelativePosZ(state.getZ());

        ChunkPos.checkChunkRelativePosition(x, (int) state.getY(), z);
        this.setBlockID(x, (int) state.getY(), z, state.getId());
        this.setBlockFacing(x, (int) state.getY(), z, state.getFacing());
        this.setBlockMeta(x, (int) state.getY(), z, state.getMeta());
    }


    public String getBiome(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.biomeSections[y / WIDTH].get(x, y % WIDTH, z);
    }

    public byte getTemperature(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.temperatureSections[y / WIDTH].get(x, y % WIDTH, z);
    }

    public byte getHumidity(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.humiditySections[y / WIDTH].get(x, y % WIDTH, z);
    }

    public void setBiome(int x, int y, int z, String biome) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.biomeSections[y / WIDTH].set(x, y % WIDTH, z,biome);
    }

    public void setTemperature(int x, int y, int z, byte temperature) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.temperatureSections[y / WIDTH].set(x, y % WIDTH, z,temperature);
    }

    public void setHumidity(int x, int y, int z, byte humidity) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.humiditySections[y / WIDTH].set(x, y % WIDTH, z,humidity);
    }

    public String getBlockID(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.blockIdSections[y / WIDTH].get(x, y % WIDTH, z);
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
        return this.lightSections[y / WIDTH].get(x, y % WIDTH, z);
    }


    public void setBlockID(int x, int y, int z, String id) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.blockIdSections[y / WIDTH].set(x, y % WIDTH, z, id);
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
        return new ChunkPos(x, z);
    }


    @Deprecated
    public void setSection(int sectionIndex, SectionLightAccess section) {

    }

    @Deprecated
    public void setSection(int sectionIndex, SectionBlockAccess section) {

    }


    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();

        for (int i = 0; i < SECTION_SIZE; i++) {
            this.compressSections(i);
            tag.setTag("block_id_section_" + i, this.blockIdSections[i].getData());
            tag.setTag("block_facing_section_" + i, this.blockFacingSections[i].getData());
            tag.setTag("block_meta_sections_" + i, this.blockMetaSections[i].getData());
            tag.setTag("light_section_" + i, this.lightSections[i].getData());
            tag.setTag("humidity_section_" + i, this.humiditySections[i].getData());
            tag.setTag("temperature_section_" + i, this.temperatureSections[i].getData());
            tag.setTag("biome_section_" + i, this.biomeSections[i].getData());
        }


        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {

    }

    public void compressSections(int i) {
        this.blockIdSections[i].tryCompress();
        this.blockFacingSections[i].tryCompress();
        this.blockMetaSections[i].tryCompress();
        this.lightSections[i].tryCompress();
        this.biomeSections[i].tryCompress();
        this.temperatureSections[i].tryCompress();
        this.humiditySections[i].tryCompress();
    }

    public StringDataSection[] getBlockIdSections() {
        return blockIdSections;
    }

    public ByteDataSection[] getBlockFacingSections() {
        return blockFacingSections;
    }

    public ByteDataSection[] getBlockMetaSections() {
        return blockMetaSections;
    }

    public ByteDataSection[] getLightSections() {
        return lightSections;
    }

    public ByteDataSection[] getHumiditySections() {
        return humiditySections;
    }

    public ByteDataSection[] getTemperatureSections() {
        return temperatureSections;
    }

    public StringDataSection[] getBiomeSections() {
        return biomeSections;
    }
}
