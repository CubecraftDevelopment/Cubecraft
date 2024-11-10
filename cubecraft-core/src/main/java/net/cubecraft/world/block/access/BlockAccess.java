package net.cubecraft.world.block.access;

import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;

import java.util.Collection;

public interface BlockAccess extends Hittable {
    int getBlockId();

    byte getBlockMeta();

    byte getBlockLight();

    Biome getBiome();

    void setBlockMeta(byte meta, boolean sendUpdateEvent);

    void setBlockLight(byte light, boolean sendUpdateEvent);

    void setBiome(String biome, boolean sendUpdateEvent);

    Block getBlock();

    void scheduleTick(int time);

    long getX();

    long getY();

    long getZ();

    IBlockAccess getNear(EnumFacing facing);

    World getWorld();

    void setBiome(Registered<Biome> biome, boolean sendUpdateEvent);

    void setBlockId(int id, boolean silent);

    @Override
    Collection<HitBox> getHitBox();

    default BlockAccess near(BlockAccessor access, int face, int range) {
        var x = getX();
        var y = getY();
        var z = getZ();

        return switch (face) {
            case 0 -> access.getBlockAccess(x, y + range, z);
            case 1 -> access.getBlockAccess(x, y - range, z);
            case 2 -> access.getBlockAccess(x, y, z + range);
            case 3 -> access.getBlockAccess(x, y, z - range);
            case 4 -> access.getBlockAccess(x + range, y, z);
            case 5 -> access.getBlockAccess(x - range, y, z);
            default -> access.getBlockAccess(x, y, z);
        };
    }

}
