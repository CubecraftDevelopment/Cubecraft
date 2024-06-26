package net.cubecraft.world.chunk.storage;

import me.gb2022.commons.container.ArrayUtil;
import me.gb2022.commons.nbt.NBTDataIO;
import net.cubecraft.world.chunk.Chunk;

public interface DataSection extends NBTDataIO {
    int SECTION_SIZE = 16;
    int DATA_SIZE = 4096;

    static void checkSectionPosition(int x, int y, int z) {
        if (x < 0 || x > SECTION_SIZE) {
            throw new RuntimeException("position out of range (X):" + x);
        }
        if (y < 0 || y > SECTION_SIZE) {
            throw new RuntimeException("position out of range (Y):" + y);
        }
        if (z < 0 || z > SECTION_SIZE) {
            throw new RuntimeException("position out of range (Z):" + z);
        }
    }

    static int calcRawDataPos3D(int x, int y, int z) {
        return ArrayUtil.calcDispatchPos3d(Chunk.WIDTH, Chunk.WIDTH, x, y, z);
    }

    static int calcRawDataPos2D(int x, int z) {
        return ArrayUtil.calcDispatchPos2d(Chunk.WIDTH, x, z);
    }
}
