package net.cubecraft.world.block.property;

import net.cubecraft.world.block.access.BlockAccess;

public class BooleanProperty implements BlockProperty<Boolean>{
    private final boolean value;

    public BooleanProperty(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean get(BlockAccess blockAccess) {
        return value;
    }


}
