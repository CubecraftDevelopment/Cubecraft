package ink.flybird.cubecraft.world.block;

import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.fcommon.math.AABB;

public interface BlockPropertyDispatcher {
    static AABB[] getCollisionBox(IBlockAccess blockAccess) {
        AABB[] local = blockAccess.getBlock().getBlockProperty("cubecraft_collision_box", AABB[].class);

        AABB[] result = new AABB[local.length];
        for (int i = 0; i < local.length; i++) {
            result[i] = local[i].cloneMove(blockAccess.getX(), blockAccess.getY(), blockAccess.getZ());
        }
        return result;
    }


}
