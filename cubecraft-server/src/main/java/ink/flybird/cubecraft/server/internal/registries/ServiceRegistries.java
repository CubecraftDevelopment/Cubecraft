package ink.flybird.cubecraft.server.internal.registries;

import ink.flybird.cubecraft.server.service.Service;
import ink.flybird.cubecraft.server.service.WorldTickService;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;

public class ServiceRegistries {
    @ItemRegisterFunc(Service.class)
    public void registerService(ConstructingMap<Service> map){

    }
}
