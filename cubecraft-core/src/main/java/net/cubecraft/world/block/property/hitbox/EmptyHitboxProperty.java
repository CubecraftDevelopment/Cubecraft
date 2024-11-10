package net.cubecraft.world.block.property.hitbox;

import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.Collection;
import java.util.Collections;

@TypeItem("cubecraft:empty_hitbox")
public class EmptyHitboxProperty extends HitBoxProperty {
    @Override
    public Collection<HitBox> get(BlockAccess block) {
        return Collections.emptyList();
    }
}
