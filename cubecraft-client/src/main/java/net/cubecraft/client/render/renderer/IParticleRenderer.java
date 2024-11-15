package net.cubecraft.client.render.renderer;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import net.cubecraft.world.entity.EntityParticle;

public interface IParticleRenderer<T extends EntityParticle> {
    void render(T particle, LegacyVertexBuilder builder, double a, double xa, double ya, double za, double xa2, double za2);
}