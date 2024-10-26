package net.cubecraft.world.chunk;

import me.gb2022.commons.nbt.NBTTagCompound;

public interface ChunkCodec {
    static NBTTagCompound getChunkData(Chunk chunk) {
        var tag = new NBTTagCompound();

        for (int i = 0; i < Chunk.SECTION_SIZE; i++) {
            NBTTagCompound sect = new NBTTagCompound();

            chunk.compressSections(i);

            sect.setTag("meta", chunk.blockMetaSections[i].getData());
            sect.setTag("light", chunk.lightSections[i].getData());

            tag.setCompoundTag("section_" + i, sect);
        }

        tag.setCompoundTag("blocks", chunk.blocks.getData());
        tag.setCompoundTag("biomes", chunk.biomes.getData());

        return tag;
    }

    static void setChunkData(Chunk chunk, NBTTagCompound tag) {
        for (int i = 0; i < Chunk.SECTION_SIZE; i++) {
            NBTTagCompound sect = tag.getCompoundTag("section_" + i);

            chunk.compressSections(i);

            chunk.blockMetaSections[i].setData(sect.getCompoundTag("meta"));
            chunk.lightSections[i].setData(sect.getCompoundTag("light"));
        }

        chunk.blocks.setData(tag.getCompoundTag("blocks"));
        chunk.biomes.setData(tag.getCompoundTag("biomes"));
    }

    static NBTTagCompound getWorldChunkData(WorldChunk chunk) {
        NBTTagCompound tag = getChunkData(chunk);
        tag.setCompoundTag("task", chunk.getTask().getData());
        tag.setEnum("state", chunk.getState());
        return tag;
    }

    static void setWorldChunkData(WorldChunk chunk, NBTTagCompound tag) {
        setChunkData(chunk, tag);
        chunk.getTask().setData(tag.getCompoundTag("task"));
        chunk.setState(tag.getEnum("state", ChunkState.class));
    }
}
