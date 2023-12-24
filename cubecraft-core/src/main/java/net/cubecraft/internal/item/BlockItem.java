package net.cubecraft.internal.item;

import net.cubecraft.world.item.Item;
import net.cubecraft.world.item.property.ItemProperty;

import java.util.Map;

public class BlockItem extends Item {
    public static final String[] BEHAVIOR_LIST = new String[]{"cubecraft:block"};

    public BlockItem(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, ItemProperty<?>> map) {
    }

    @Override
    public String[] getBehaviorList() {
        return BEHAVIOR_LIST;
    }
}
