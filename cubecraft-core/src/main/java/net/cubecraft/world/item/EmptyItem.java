package net.cubecraft.world.item;

import net.cubecraft.world.item.property.ItemProperty;

import java.util.Map;

public class EmptyItem extends Item {
    public EmptyItem(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, ItemProperty<?>> map) {

    }

    @Override
    public String[] getBehaviorList() {
        return new String[]{"cubecraft:diggable"};
    }
}
