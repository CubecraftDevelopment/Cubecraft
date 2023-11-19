package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.attribute.SimpleBooleanProperty;

import java.util.Map;

public class LiquidBlock extends Block {
    public LiquidBlock(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
        map.put("cubecraft:solid", new SimpleBooleanProperty(false));
    }

    @Override
    public String[] getBehaviorList() {
        return new String[0];
    }

    @Override
    public String[] getTags() {
        return new String[]{"cubecraft:liquid"};
    }
}
