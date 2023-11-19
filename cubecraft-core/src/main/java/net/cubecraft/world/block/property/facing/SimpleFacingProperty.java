package net.cubecraft.world.block.property.facing;

import net.cubecraft.world.block.EnumFacing;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:simple_facing")
public final class SimpleFacingProperty extends FixedFacingProperty {
    public SimpleFacingProperty() {
        super(EnumFacing.Up);
    }
}
