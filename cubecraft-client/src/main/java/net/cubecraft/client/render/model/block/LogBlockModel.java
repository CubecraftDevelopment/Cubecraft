package net.cubecraft.client.render.model.block;

import com.google.gson.JsonObject;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.model.CullingMethod;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.util.DeserializedConstructor;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;

import java.util.Objects;
import java.util.Set;

@TypeItem("cubecraft:log")
public final class LogBlockModel extends BlockModel {
    private final TextureAsset texture;
    private final TextureAsset top;
    private final String layer;
    private final CullingMethod culling;

    public LogBlockModel(TextureAsset tex, TextureAsset top, String layer, CullingMethod culling) {
        this.texture = tex;
        this.top = top;
        this.layer = layer;
        this.culling = culling;
    }

    @DeserializedConstructor
    public LogBlockModel(JsonObject json) {
        this.texture = new TextureAsset(json.get("texture").getAsString());
        this.top = new TextureAsset(json.get("top_texture").getAsString());
        this.layer = json.get("layer").getAsString();
        this.culling = CullingMethod.from(json.get("culling").getAsString());
    }

    public static boolean shouldRender(CullingMethod culling, int current, IBlockAccess blockAccess, BlockAccessor world, long x, long y, long z) {
        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, current);
        IBlockAccess near = world.getBlockAccess(pos.x(), pos.y(), pos.z());
        return switch (culling) {
            case NEVER -> false;
            case SOLID -> !BlockPropertyDispatcher.isSolid(near);
            case ALWAYS -> true;
            case SOLID_OR_EQUALS -> !BlockPropertyDispatcher.isSolid(near) &&
                    !(Objects.equals(near.getBlockID(), blockAccess.getBlockID()));
            case EQUALS -> !(Objects.equals(near.getBlockID(), blockAccess.getBlockID()));
        };
    }

    @Override
    public void initializeModel(Set<TextureAsset> textureList) {
        textureList.add(this.texture);
        textureList.add(this.top);
    }

    @Override
    public String getParticleTexture() {
        return this.texture.getAbsolutePath();
    }

    @Override
    public void renderBlock(IBlockAccess block, String layer, BlockAccessor world, int face, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        if (!Objects.equals(layer, this.layer)) {
            return;
        }

        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();

        if (shouldRender(this.culling, face, block, world, x, y, z)) {
            if (face == 0 || face == 1) {
                IBlockRenderer.renderFace(face, this.top.getAbsolutePath(), null, builder, world, x, y, z, renderX, renderY, renderZ);
                return;
            }

            IBlockRenderer.renderFace(face, this.texture.getAbsolutePath(), null, builder, world, x, y, z, renderX, renderY, renderZ);
        }
    }
}
