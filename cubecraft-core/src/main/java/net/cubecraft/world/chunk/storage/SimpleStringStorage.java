package net.cubecraft.world.chunk.storage;


import ink.flybird.fcommon.nbt.NBTTagCompound;
import net.cubecraft.util.DynamicNameIdMap;

public class SimpleStringStorage implements StringStorage {
    private final DynamicNameIdMap data;

    public SimpleStringStorage(StringStorage storage) {
        this.data = new DynamicNameIdMap(DataSection.DATA_SIZE);
        this.data.fill(storage.get(0, 0, 0));
    }

    public SimpleStringStorage(DynamicNameIdMap data) {
        this.data = data;
    }

    public SimpleStringStorage() {
        data = new DynamicNameIdMap(DataSection.DATA_SIZE);
    }

    public String[] getArray() {
        return this.data.getArray();
    }

    public NBTTagCompound getData() {
        return data.export();
    }

    public void setData(NBTTagCompound data) {
        this.data.setData(data);
    }

    public String get(int x, int y, int z) {
        return this.data.get(DataSection.calcRawDataPos3D(x, y, z));
    }

    public void set(int x, int y, int z, String i) {
        this.data.set(DataSection.calcRawDataPos3D(x, y, z), i);
    }

    public DynamicNameIdMap getMap() {
        return this.data;
    }
}
