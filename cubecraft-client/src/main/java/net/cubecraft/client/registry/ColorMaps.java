package net.cubecraft.client.registry;

import me.gb2022.commons.registry.RegisterMap;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.util.register.Registered;

public interface ColorMaps {
    NamedRegistry<ColorMap> REGISTRY = new NamedRegistry<>();

    Registered<ColorMap> FOLIAGE=REGISTRY.register("cubecraft:foliage",ColorMapRegistry.foliage());
    RegisterMap<ColorMap> COLOR_MAP = new RegisterMap<>(ColorMap.class);
}
