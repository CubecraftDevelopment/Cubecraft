package ink.flybird.cubecraft.world.block.property.collision;

import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:cube_collision")
public class CubeCollisionProperty extends CollisionProperty {
    public static final AABB[] DEFAULT_CUBE_AABB = new AABB[]{
            new AABB(0, 0, 0, 1, 1, 1)
    };

    @Override
    public AABB[] get(IBlockAccess access) {
        return DEFAULT_CUBE_AABB;
    }
}
