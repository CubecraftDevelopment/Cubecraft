package net.cubecraft.world.block.behavior;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface BlockBehaviorRegistry {
    @FieldRegistry("gravity")
    BlockBehavior GRAVITY = new GravityBlockBehavior();
}
