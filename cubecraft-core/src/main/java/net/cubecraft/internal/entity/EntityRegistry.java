package net.cubecraft.internal.entity;

import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import net.cubecraft.world.entity.Entity;

public class EntityRegistry {
    @ItemRegisterFunc(Entity.class)
    public void reg(ConstructingMap<Entity> map){
        map.registerItem(EntityPlayer.class);
    }
}
