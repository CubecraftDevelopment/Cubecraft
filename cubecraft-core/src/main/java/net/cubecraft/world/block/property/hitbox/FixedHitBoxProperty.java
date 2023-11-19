package net.cubecraft.world.block.property.hitbox;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.hitting.HitBox;
import ink.flybird.fcommon.registry.TypeItem;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.ArrayList;
import java.util.Collection;

@TypeItem("cubecraft:fixed_hitbox")
public class FixedHitBoxProperty extends HitBoxProperty {
    private final AABB[] aabbs;

    public FixedHitBoxProperty(AABB... aabbs) {
        this.aabbs = aabbs;
    }

    @Override
    public Collection<HitBox> get(IBlockAccess block) {
        Collection<HitBox> result = new ArrayList<>();
        for (AABB aabb : this.aabbs) {
            result.add(new HitBox("block_default", aabb.cloneMove(block.getX(), block.getY(), block.getZ())));
        }
        return result;
    }
}
