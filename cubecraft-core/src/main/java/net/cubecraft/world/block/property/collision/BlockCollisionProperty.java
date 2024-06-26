package net.cubecraft.world.block.property.collision;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.TypeItem;

@TypeItem("cubecraft:cube_collision")
public class BlockCollisionProperty extends FixedCollisionProperty {
    public static final AABB[] DEFAULT_CUBE_AABB = new AABB[]{
            new AABB(0, 0, 0, 1, 1, 1)
    };

    public BlockCollisionProperty() {
        super(DEFAULT_CUBE_AABB);
    }
}
