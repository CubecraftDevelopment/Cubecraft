package net.cubecraft.world.environment;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface DimensionRegistry {
    @FieldRegistry("overworld")
    Environment OVERWORLD = new OverworldEnvironment();
}
