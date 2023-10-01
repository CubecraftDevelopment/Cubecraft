package ink.flybird.cubecraft.world.block.access;

import ink.flybird.cubecraft.internal.block.BlockType;
import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.block.Block;
import ink.flybird.cubecraft.world.block.EnumFacing;

public class NonLoadedBlockAccess extends IBlockAccess {
    public NonLoadedBlockAccess(IWorld world, long x, long y, long z) {
        super(world, x, y, z);
    }

    @Override
    public String getBlockID() {
        return this.dimension.predictBlockID(this.world, this.x, this.y, this.z);
    }

    @Override
    public EnumFacing getBlockFacing() {
        return EnumFacing.Up;
    }

    @Override
    public byte getBlockMeta() {
        return this.dimension.predictBlockMetaAt(this.world, this.x, this.y, this.z);
    }

    @Override
    public byte getBlockLight() {
        return this.dimension.predictLightAt(this.world, this.x, this.y, this.z);
    }

    @Override
    public String getBiome() {
        return null;
    }

    @Override
    public byte getTemperature() {
        return 0;
    }

    @Override
    public byte getHumidity() {
        return 0;
    }

    @Override
    public Block getBlock() {
        //todo:fix data fetch
        return ContentRegistries.BLOCK.get(BlockType.AIR);
    }
}
