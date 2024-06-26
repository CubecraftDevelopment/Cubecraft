package net.cubecraft.world.block.property.collision;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.ArrayList;
import java.util.Collection;

@TypeItem("cubecraft:fixed_collision")
public class FixedCollisionProperty extends CollisionProperty {
    private final AABB[] aabbs;

    public FixedCollisionProperty(AABB... aabbs) {
        this.aabbs = aabbs;
    }

    @Override
    public Collection<AABB> get(IBlockAccess block) {
        Collection<AABB> result = new ArrayList<>();
        for (AABB aabb : this.aabbs) {
            result.add(aabb.cloneMove(block.getX(), block.getY(), block.getZ()));
        }
        return result;
    }
}
