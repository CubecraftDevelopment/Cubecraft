package net.cubecraft.client.internal.entity;

import net.cubecraft.world.World;
import net.cubecraft.world.entity.EntityParticle;
import me.gb2022.commons.registry.TypeItem;

@TypeItem("cubecraft:block_brake_particle")
public class BlockBrakeParticle extends EntityParticle {
    public BlockBrakeParticle(World world, double x, double y, double z, double xa, double ya, double za, String block) {
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
