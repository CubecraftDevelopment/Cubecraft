package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.attribute.SimpleBooleanProperty;

import java.util.Map;

public final class BlockAir extends Block {
    public BlockAir() {
        super("cubecraft:air");
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
        map.put("cubecraft:solid", new SimpleBooleanProperty(false));
    }

    @Override
    public String[] getBehaviorList() {
        return new String[0];
    }
}
