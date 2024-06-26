package net.cubecraft.world.dimension;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface DimensionRegistry {
    @FieldRegistry("overworld")
    Dimension OVERWORLD = new DimensionOverworld();
}
