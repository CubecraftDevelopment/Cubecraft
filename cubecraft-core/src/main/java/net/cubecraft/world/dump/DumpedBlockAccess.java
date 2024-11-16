package net.cubecraft.world.dump;

import net.cubecraft.internal.world.biome.BiomesRegistry;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.access.BlockAccess;

public class DumpedBlockAccess implements BlockAccess {
    private final BlockAccessor accessor;
    private final long x, y, z;
    private final int block;
    private final byte meta;
    private final byte light;


    public DumpedBlockAccess(BlockAccessor accessor, long x, long y, long z, int block, byte meta, byte light) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.meta = meta;
        this.light = light;
        this.accessor = accessor;
    }

    public static DumpedBlockAccess dump(BlockAccess access) {
        return new DumpedBlockAccess(access.getWorld(),
                                     access.getX(),
                                     access.getY(),
                                     access.getZ(),
                                     access.getBlockId(),
                                     access.getBlockMeta(),
                                     access.getBlockLight()
        );
    }

    public static DumpedBlockAccess dump(BlockAccessor accessor, long x, long y, long z) {
        return new DumpedBlockAccess(accessor,
                                     x,
                                     y,
                                     z,
                                     accessor.getBlockId(x, y, z),
                                     accessor.getBlockMetadata(x, y, z),
                                     accessor.getBlockLight(x, y, z)
        );
    }

    @Override
    public int getBlockId() {
        return this.block;
    }

    @Override
    public byte getBlockMeta() {
        return this.meta;
    }

    @Override
    public byte getBlockLight() {
        return this.light;
    }

    @Override
    public Biome getBiome() {
        return BiomesRegistry.PLAINS;
    }

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }

    @Override
    public long getZ() {
        return z;
    }

    @Override
    public BlockAccessor getWorld() {
        return this.accessor;
    }
}
