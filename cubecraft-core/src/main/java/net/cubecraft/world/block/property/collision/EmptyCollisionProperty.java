package net.cubecraft.world.block.property.collision;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.Collection;
import java.util.Collections;

@TypeItem("cubecraft:empty_collision")
public class EmptyCollisionProperty extends CollisionProperty {
    public static final AABB[] DUMMY = new AABB[0];

    @Override
    public Collection<AABB> get(BlockAccess block) {
        return Collections.emptyList();
    }
}
