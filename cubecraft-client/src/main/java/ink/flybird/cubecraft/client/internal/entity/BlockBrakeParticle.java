package ink.flybird.cubecraft.client.internal.entity;

import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.entity.EntityParticle;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:block_brake_particle")
public class BlockBrakeParticle extends EntityParticle {
    public BlockBrakeParticle(IWorld world, double x, double y, double z, double xa, double ya, double za, String block) {
        super(world, x, y, z, xa, ya, za, block);
    }

    @Override
    public float getSize() {
        return 4;
    }

    @Override
    public float getLifetime() {
        return 5;
    }
}
