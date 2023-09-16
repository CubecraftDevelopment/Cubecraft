package ink.flybird.cubecraft.world.chunk;

import ink.flybird.fcommon.nbt.NBTTagCompound;

public interface ChunkCodec {
    static NBTTagCompound getChunkData(Chunk chunk) {
        NBTTagCompound tag = new NBTTagCompound();

        for (int i = 0; i < Chunk.SECTION_SIZE; i++) {
            tag.setCompoundTag("section" + i, getChunkSection(chunk, i));
        }

        return tag;
    }

    static void setChunkData(Chunk chunk, NBTTagCompound tag) {
        for (int i = 0; i < Chunk.SECTION_SIZE; i++) {
            setChunkSection(chunk, i, tag.getCompoundTag("section" + i));
        }
    }

    static NBTTagCompound getWorldChunkData(WorldChunk chunk) {
        NBTTagCompound tag = getChunkData(chunk);
        tag.setCompoundTag("task", chunk.getTask().getData());
        return tag;
    }

    static void setWorldChunkData(WorldChunk chunk, NBTTagCompound tag) {
        setChunkData(chunk, tag);
        chunk.getTask().setData(tag.getCompoundTag("task"));
    }

    static NBTTagCompound getChunkSection(Chunk chunk, int i) {
        NBTTagCompound tag = new NBTTagCompound();

        chunk.compressSections(i);
        tag.setTag("block_id_section", chunk.blockIdSections[i].getData());
        tag.setTag("block_facing_section", chunk.blockFacingSections[i].getData());
        tag.setTag("block_meta_sections", chunk.blockMetaSections[i].getData());
        tag.setTag("light_section", chunk.lightSections[i].getData());
        tag.setTag("humidity_section", chunk.humiditySections[i].getData());
        tag.setTag("temperature_section", chunk.temperatureSections[i].getData());
        tag.setTag("biome_section", chunk.biomeSections[i].getData());

        return tag;
    }

    static void setChunkSection(Chunk chunk, int i, NBTTagCompound tag) {
        chunk.blockIdSections[i].setData(tag.getCompoundTag("block_id_section"));
        chunk.blockFacingSections[i].setData(tag.getCompoundTag("block_facing_section"));
        chunk.blockMetaSections[i].setData(tag.getCompoundTag("block_meta_sections"));
        chunk.lightSections[i].setData(tag.getCompoundTag("light_section"));
        chunk.humiditySections[i].setData(tag.getCompoundTag("humidity_section"));
        chunk.temperatureSections[i].setData(tag.getCompoundTag("temperature_section"));
        chunk.biomeSections[i].setData(tag.getCompoundTag("biome_section"));
        chunk.compressSections(i);
    }
}
