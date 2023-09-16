package ink.flybird.cubecraft.world.chunk.storage;

import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.fcommon.nbt.NBTTagCompound;

import java.util.Arrays;

public class SimpleLightSection implements DataSection, SectionLightAccess {
    private byte[] light = new byte[DATA_SIZE];

    public SimpleLightSection(UnifiedLightSection section) {
        Arrays.fill(this.light, section.getBlockLight(0,0,0));
    }

    public SimpleLightSection(){
        Arrays.fill(this.light, (byte) 0);
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        return this.light[DataSection.calcRawDataPos3D(x, y, z)];
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        ChunkPos.checkChunkRelativePosition(x, y, z);
        this.light[DataSection.calcRawDataPos3D(x, y, z)] = light;
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("light", this.light);
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.light = tag.getByteArray("light");
    }

}
