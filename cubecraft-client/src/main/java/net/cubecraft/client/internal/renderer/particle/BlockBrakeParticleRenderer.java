package net.cubecraft.client.internal.renderer.particle;

import ink.flybird.quantum3d_legacy.draw.LegacyVertexBuilder;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.render.renderer.IParticleRenderer;

@TypeItem("cubecraft:block_brake")
public class BlockBrakeParticleRenderer implements IParticleRenderer<BlockBrakeParticle> {

    @Override
    public void render(BlockBrakeParticle particle, LegacyVertexBuilder builder, double a, double xa, double ya, double za, double xa2, double za2) {
        Texture2DTileMap tex = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().get("cubecraft:terrain");
        float u0 = tex.exactTextureU(particle.getTexture(), particle.getUOffset() / 4f);
        float u1 = tex.exactTextureU(particle.getTexture(), particle.getUOffset() / 4f + 0.25f);
        float v0 = tex.exactTextureV(particle.getTexture(), particle.getVOffset() / 4f);
        float v1 = tex.exactTextureV(particle.getTexture(), particle.getVOffset() / 4f + 0.25f);
        double r = 0.1f * particle.getSize() / 4;
        double x = particle.xo + (particle.x - particle.xo) * a;
        double y = particle.yo + (particle.y - particle.yo) * a;
        double z = particle.zo + (particle.z - particle.zo) * a;
        tex.bind();
        builder.vertexUV(x - xa * r - xa2 * r - particle.x, y - ya * r - particle.y, z - za * r - za2 * r - particle.z, u0, v1);
        builder.vertexUV(x - xa * r + xa2 * r - particle.x, y + ya * r - particle.y, z - za * r + za2 * r - particle.z, u0, v0);
        builder.vertexUV(x + xa * r + xa2 * r - particle.x, y + ya * r - particle.y, z + za * r + za2 * r - particle.z, u1, v0);
        builder.vertexUV(x + xa * r - xa2 * r - particle.x, y - ya * r - particle.y, z + za * r - za2 * r - particle.z, u1, v1);
        tex.unbind();
    }
}