package net.cubecraft.world.block.property.attribute;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BooleanProperty;

@TypeItem("cubecraft:solid")
public final class SolidProperty extends BooleanProperty {
    @Override
    public Boolean get(IBlockAccess blockAccess) {
        return true;
    }
}
