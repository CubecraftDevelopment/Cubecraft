package net.cubecraft.world.block.property.attribute;

import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BooleanProperty;

public final class SimpleBooleanProperty extends BooleanProperty {
    private final boolean value;

    public SimpleBooleanProperty(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean get(IBlockAccess blockAccess) {
        return this.value;
    }
}
