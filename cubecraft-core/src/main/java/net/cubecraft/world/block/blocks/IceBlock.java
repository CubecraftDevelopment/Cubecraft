package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;

import java.util.Map;

public class IceBlock extends Block {
    public IceBlock(String s) {
        super(s);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {

    }

    @Override
    public String[] getBehaviorList() {
        return new String[0];
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
