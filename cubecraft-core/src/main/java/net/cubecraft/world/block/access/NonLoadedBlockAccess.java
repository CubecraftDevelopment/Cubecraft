package net.cubecraft.world.block.access;

import net.cubecraft.util.register.Registered;
import net.cubecraft.world.World;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.Blocks;

public class NonLoadedBlockAccess extends IBlockAccess {
    public NonLoadedBlockAccess(World world, long x, long y, long z) {
        super(world, x, y, z);
    }

    @Override
    public int getBlockId() {
        return Blocks.AIR.getId();
    }

    @Override
    public byte getBlockMeta() {
        return 0;
    }

    @Override
    public byte getBlockLight() {
        return 0;
    }

    @Override
    public Biome getBiome() {
        return null;
    }

    @Override
    public Block getBlock() {
        return Blocks.AIR.get();
    }

    @Override
    public void setBiome(Registered<Biome> biome, boolean sendUpdateEvent) {

    }
}
