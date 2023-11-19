package net.cubecraft.server.internal.registries;

import net.cubecraft.server.service.Service;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;

public class ServiceRegistries {
    @ItemRegisterFunc(Service.class)
    public void registerService(ConstructingMap<Service> map){

    }
}
