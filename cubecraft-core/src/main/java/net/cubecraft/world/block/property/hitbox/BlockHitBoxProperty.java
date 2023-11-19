package net.cubecraft.world.block.property.hitbox;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:cube_collision")
public class BlockHitBoxProperty extends FixedHitBoxProperty {
    public static final AABB[] DEFAULT_CUBE_AABB = new AABB[]{
            new AABB(0, 0, 0, 1, 1, 1)
    };

    public BlockHitBoxProperty() {
        super(DEFAULT_CUBE_AABB);
    }
}
