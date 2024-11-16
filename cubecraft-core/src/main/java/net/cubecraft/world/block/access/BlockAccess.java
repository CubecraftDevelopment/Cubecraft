package net.cubecraft.world.block.access;

import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;

import java.util.Collection;
import java.util.List;

public interface BlockAccess extends Hittable {
    int getBlockId();

    byte getBlockMeta();

    byte getBlockLight();

    Biome getBiome();

    default void setBlockMeta(byte meta, boolean sendUpdateEvent) {
    }

    default void setBlockLight(byte light, boolean sendUpdateEvent) {
    }

    default void setBiome(String biome, boolean sendUpdateEvent) {
    }

    default Block getBlock(){
        return Blocks.REGISTRY.object(getBlockId());
    }

    default void scheduleTick(int time) {
    }

    long getX();

    long getY();

    long getZ();


    BlockAccessor getWorld();

    default void setBiome(Registered<Biome> biome, boolean sendUpdateEvent) {
    }

    default void setBlockId(int id, boolean silent) {
    }

    @Override
    default Collection<HitBox> getHitBox() {
        return BlockPropertyDispatcher.getHitBox(this);
    }

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

    default BlockAccess getNear(EnumFacing facing) {
        Vector3<Long> pos = facing.findNear(this.getX(), this.getY(), this.getZ(), 1);
        return this.getWorld().getBlockAccess(pos.x(), pos.y(), pos.z());
    }
}
