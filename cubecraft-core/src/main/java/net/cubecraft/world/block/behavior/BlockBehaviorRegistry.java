package net.cubecraft.world.block.behavior;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface BlockBehaviorRegistry {
    @FieldRegistry("gravity")
    BlockBehavior GRAVITY = new GravityBlockBehavior();
}
