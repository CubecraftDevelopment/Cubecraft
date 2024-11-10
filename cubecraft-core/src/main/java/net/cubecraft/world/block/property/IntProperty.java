package net.cubecraft.world.block.property;

import net.cubecraft.world.block.access.BlockAccess;

public class IntProperty implements BlockProperty<Integer> {
    private final int value;

    public IntProperty(int value) {
        this.value = value;
    }

    @Override
    public Integer get(BlockAccess blockAccess) {
        return value;
    }
}
