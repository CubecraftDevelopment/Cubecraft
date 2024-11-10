package net.cubecraft.world.item;

import me.gb2022.commons.Initializable;
import me.gb2022.commons.math.hitting.HitResult;
import net.cubecraft.ContentRegistries;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.behavior.ItemBehavior;
import net.cubecraft.world.item.property.ItemProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Item implements Initializable {
    private final HashMap<String, ItemProperty<?>> properties = new HashMap<>(64);
    private final HashMap<String, ItemBehavior> behaviors = new HashMap<>(64);
    private final String id;

    public Item(String id) {
        this.id = id;
    }

    public Item(String[] behaviorList, String id) {
        this.id = id;
    }

    private void applyData(String[] behaviorList) {
        this.initPropertyMap(this.properties);
        for (String id : behaviorList) {
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

    public abstract void initPropertyMap(Map<String, ItemProperty<?>> map);

    public abstract String[] getBehaviorList();

    public void onDig(HitResult result, BlockAccess block) {
        for (ItemBehavior behavior : getBehaviors()) {
            behavior.onDig(result, this, block);
        }
    }

    private List<ItemBehavior> getBehaviors() {
        List<ItemBehavior> behaviors = new ArrayList<>();
        for (String s : this.getBehaviorList()) {
            ItemBehavior behavior = ContentRegistries.ITEM_BEHAVIOR.get(s);
            if (behavior == null) {
                continue;
            }
            behaviors.add(behavior);
        }
        return behaviors;
    }

    public void onUse(HitResult result, BlockAccess block) {
        for (ItemBehavior behavior : this.getBehaviors()) {
            behavior.onUse(result, this, block);
        }
    }

    public void onAttack(HitResult result, Entity entity) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onAttack(result, this, entity);
        }
    }

    public void onUse(HitResult result, Entity entity) {
        for (ItemBehavior behavior : this.behaviors.values()) {
            behavior.onUse(result, this, entity);
        }
    }

    public <I> ItemProperty<I> getProperty(String id, Class<? extends ItemProperty<I>> clazz) {
        return clazz.cast(this.properties.get(id));
    }

    public ItemBehavior getBehavior(String id) {
        return this.behaviors.get(id);
    }

    @Override
    public void init() {
        this.applyData(this.getBehaviorList());
    }
}
