package net.cubecraft.world.block.property.collision;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:cube_collision")
public class BlockCollisionProperty extends FixedCollisionProperty {
    public static final AABB[] DEFAULT_CUBE_AABB = new AABB[]{
            new AABB(0, 0, 0, 1, 1, 1)
    };

    public BlockCollisionProperty() {
        super(DEFAULT_CUBE_AABB);
    }
}
