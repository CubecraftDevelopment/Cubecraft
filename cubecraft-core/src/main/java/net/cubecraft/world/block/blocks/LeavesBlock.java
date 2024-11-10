package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.BooleanProperty;

import java.util.Map;

public class LeavesBlock extends Block {
    public LeavesBlock(String string) {
        super(string);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
        map.put("cubecraft:solid", new BooleanProperty(false));
    }

    @Override
    public String[] getBehaviorList() {
        return new String[]{"cubecraft:face_top_only", "cubecraft:drop_with_silk_touch_or_default"};
    }
}
