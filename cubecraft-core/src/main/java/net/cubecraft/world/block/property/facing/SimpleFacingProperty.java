package net.cubecraft.world.block.property.facing;

import net.cubecraft.world.block.EnumFacing;
import me.gb2022.commons.registry.TypeItem;

@TypeItem("cubecraft:simple_facing")
public final class SimpleFacingProperty extends FixedFacingProperty {
    public SimpleFacingProperty() {
        super(EnumFacing.Up);
    }
}
