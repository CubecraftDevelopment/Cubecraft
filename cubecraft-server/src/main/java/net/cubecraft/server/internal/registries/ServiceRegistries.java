package net.cubecraft.server.internal.registries;

import net.cubecraft.server.service.Service;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;

public class ServiceRegistries {
    @ItemRegisterFunc(Service.class)
    public void registerService(ConstructingMap<Service> map){

    }
}
