package net.cubecraft.internal.entity;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;
import net.cubecraft.world.entity.Entity;

public class EntityRegistry {
    @ItemRegisterFunc(Entity.class)
    public void reg(ConstructingMap<Entity> map){
        map.registerItem(EntityPlayer.class);
    }
}
