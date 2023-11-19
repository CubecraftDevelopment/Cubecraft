package net.cubecraft.internal.item;

import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.item.Item;

import java.util.Map;

public class BlockItem extends Item {
    public static final String[] BEHAVIOR_LIST = new String[]{"cubecraft:block"};

    public BlockItem(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {

    }

    @Override
    public String[] getBehaviorList() {
        return BEHAVIOR_LIST;
    }
}
