package io.flybird.cubecraft.world.chunk.storage;

import io.flybird.cubecraft.internal.block.BlockType;
import io.flybird.cubecraft.world.block.EnumFacing;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.fcommon.container.DynamicNameIdMap;
import ink.flybird.fcommon.nbt.NBTTagCompound;

import java.util.Arrays;

public class SimpleBlockSection implements SectionBlockAccess, DataSection {
    public static final String NBT_BLOCK_ID = "block_id";
    public static final String NBT_BLOCK_FACING = "block_facing";
    public static final String NBT_BLOCK_META = "block_meta";

    private final DynamicNameIdMap id = new DynamicNameIdMap(DATA_SIZE);
    private byte[] meta = new byte[DATA_SIZE];
    private byte[] facing = new byte[DATA_SIZE];

    public SimpleBlockSection(UnifiedBlockSection section) {
        this.id.fill(section.getBlockID(0, 0, 0));
        Arrays.fill(this.meta, section.getBlockMeta(0, 0, 0));
        Arrays.fill(this.facing, section.getBlockFacing(0, 0, 0).getNumID());
    }

    public SimpleBlockSection() {
        this.id.fill(BlockType.AIR);
        Arrays.fill(this.facing, (byte) 0);
        Arrays.fill(this.meta, (byte) 0);
    }

    @Override
    public String getBlockID(int x, int y, int z) {
        ChunkPos.checkChunkSectionRelativePosition(x, y, z);
        return this.id.get(DataSection.calcRawDataPos3D(x, y, z));
    }

    @Override
    public EnumFacing getBlockFacing(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return EnumFacing.fromId(this.facing[DataSection.calcRawDataPos3D(x, y, z)]);
    }

    @Override
    public byte getBlockMeta(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.meta[DataSection.calcRawDataPos2D(x, z)];
    }

    @Override
    public void setBlockID(int x, int y, int z, String id) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.id.set(DataSection.calcRawDataPos3D(x, y, z), id);
    }

    @Override
    public void setBlockFacing(int x, int y, int z, EnumFacing facing) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.facing[DataSection.calcRawDataPos3D(x, y, z)] = facing.getNumID();
    }

    @Override
    public void setBlockMeta(int x, int y, int z, byte meta) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.meta[DataSection.calcRawDataPos3D(x, y, z)] = meta;
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setCompoundTag(NBT_BLOCK_ID, this.id.export());
        tag.setByteArray(NBT_BLOCK_FACING, this.facing);
        tag.setByteArray(NBT_BLOCK_META, this.meta);
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.id.setData(tag.getCompoundTag(NBT_BLOCK_ID));
        this.facing = tag.getByteArray(NBT_BLOCK_FACING);
        this.meta = tag.getByteArray(NBT_BLOCK_META);
    }
}
