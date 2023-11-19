package net.cubecraft.world.dimension;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface DimensionRegistry {
    @FieldRegistry("overworld")
    Dimension OVERWORLD = new DimensionOverworld();
}
