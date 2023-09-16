package ink.flybird.cubecraft.server.internal.registries;

import ink.flybird.cubecraft.server.service.AbstractService;
import ink.flybird.cubecraft.server.internal.service.WorldTickService;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;

public class ServiceRegistries {
    @ItemRegisterFunc(AbstractService.class)
    public void registerService(ConstructingMap<AbstractService> map){
        map.registerItem(WorldTickService.class);
    }
}
