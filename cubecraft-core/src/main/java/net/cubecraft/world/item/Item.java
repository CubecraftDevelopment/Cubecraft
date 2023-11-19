package net.cubecraft.world.item;

import net.cubecraft.ContentRegistries;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.behavior.ItemBehavior;

import java.util.HashMap;
import java.util.Map;

public abstract class Item implements ItemBehavior {
    private final HashMap<String, BlockProperty<?>> properties = new HashMap<>(64);
    private final HashMap<String, ItemBehavior> behaviors = new HashMap<>(64);
    private final String id;

    public Item(String id) {
        this.id = id;
        this.applyData(this.getBehaviorList());
    }

    public Item(String[] behaviorList, String id) {
        this.id = id;
        this.applyData(behaviorList);
    }

    private void applyData(String[] behaviorList) {
        this.initPropertyMap(this.properties);
        for (String id : this.getBehaviorList()) {
            ItemBehavior behavior = ContentRegistries.ITEM_BEHAVIOR.get(this.getId());
            if (behavior == null) {
                continue;
            }
            this.behaviors.put(id, behavior);
        }
    }

    public String getId() {
        return this.id;
    }

    public abstract void initPropertyMap(Map<String, BlockProperty<?>> map);

    public abstract String[] getBehaviorList();


    @Override
    public void onDig(Item item, IBlockAccess block) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onDig(item, block);
        }
    }

    @Override
    public void onUse(Item item, IBlockAccess block) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onUse(item, block);
        }
    }

    @Override
    public void onAttack(Item item, Entity entity) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onAttack(item, entity);
        }
    }

    @Override
    public void onUse(Item item, Entity entity) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onUse(item, entity);
        }
    }
}
