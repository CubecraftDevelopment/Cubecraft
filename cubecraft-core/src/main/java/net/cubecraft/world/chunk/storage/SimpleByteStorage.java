package net.cubecraft.world.chunk.storage;

import java.util.Arrays;

public class SimpleByteStorage implements ByteStorage {
    private final byte[] data;

    public SimpleByteStorage(ByteStorage storage) {
        this.data = new byte[DataSection.DATA_SIZE];
        Arrays.fill(this.data, storage.get(0, 0, 0));
    }

    public SimpleByteStorage(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

    public byte get(int x, int y, int z) {
        return this.data[DataSection.calcRawDataPos3D(x, y, z)];
    }

    public void set(int x, int y, int z, byte i) {
        this.data[DataSection.calcRawDataPos3D(x, y, z)] = i;
    }
}
