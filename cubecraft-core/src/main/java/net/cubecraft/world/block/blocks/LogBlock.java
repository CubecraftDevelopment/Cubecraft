package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;

import java.util.Map;

public class LogBlock extends Block {
    public LogBlock(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
    }

    @Override
    public String[] getBehaviorList() {
        return new String[]{
                "cubecraft:face_top_only",
                "cubecraft:drop_with_silk_touch_or_default"
        };
    }
}
