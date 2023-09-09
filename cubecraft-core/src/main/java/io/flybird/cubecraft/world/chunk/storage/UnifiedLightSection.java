package io.flybird.cubecraft.world.chunk.storage;

import ink.flybird.fcommon.nbt.NBTTagCompound;

public class UnifiedLightSection implements DataSection, SectionLightAccess {
    private byte light;

    public UnifiedLightSection(byte light) {
        this.light = light;
    }

    public UnifiedLightSection(SimpleLightSection section) {
        this(section.getBlockLight(0, 0, 0));
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        return this.light;
    }

    @Override
    public void setBlockLight(int x, int y, int z, byte light) {
        throw new UnsupportedOperationException("full light section,unzip before set value!");
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("light", this.light);
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.light = tag.getByte("light");
    }
}
