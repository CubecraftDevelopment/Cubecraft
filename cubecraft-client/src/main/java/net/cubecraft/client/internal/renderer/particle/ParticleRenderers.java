package net.cubecraft.client.internal.renderer.particle;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import net.cubecraft.client.render.renderer.IParticleRenderer;

@FieldRegistryHolder("cubecraft")
public interface ParticleRenderers {
    @FieldRegistry("block_brake_particle")
    IParticleRenderer<?> BLOCK_BRAKE = new BlockBrakeParticleRenderer();
}
