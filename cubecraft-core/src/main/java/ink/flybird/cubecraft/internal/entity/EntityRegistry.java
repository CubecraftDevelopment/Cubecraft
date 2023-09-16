package ink.flybird.cubecraft.internal.entity;

import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.cubecraft.world.entity.Entity;

public class EntityRegistry {
    @ItemRegisterFunc(Entity.class)
    public void reg(ConstructingMap<Entity> map){
        map.registerItem(EntityPlayer.class);
    }
}
