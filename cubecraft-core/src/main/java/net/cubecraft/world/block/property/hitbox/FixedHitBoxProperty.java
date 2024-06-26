package net.cubecraft.world.block.property.hitbox;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.registry.TypeItem;
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
