package net.cubecraft.client.render.block;

import com.google.gson.JsonObject;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.registry.RegisteredConstructor;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;

import java.util.Objects;
import java.util.Set;

@TypeItem("cubecraft:block")
public final class BlockRenderer implements IBlockRenderer {
    private final TextureAsset texture;
    private final String layer;

    public BlockRenderer(TextureAsset tex, String layer) {
        this.texture = tex;
        this.layer = layer;
    }

    @RegisteredConstructor
    public BlockRenderer(JsonObject json) {
        this.texture = new TextureAsset(json.get("texture").getAsString());
        this.layer = json.get("layer").getAsString();
    }

    @Override
    public void renderBlock(IBlockAccess blockAccess, String layer, World world, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        long x = blockAccess.getX();
        long y = blockAccess.getY();
        long z = blockAccess.getZ();
        if (!Objects.equals(layer, this.layer)) {
            return;
        }
        for (int face = 0; face < 6; face++) {
            if (this.shouldRender(face, blockAccess, world, x, y, z)) {
                IBlockRenderer.renderFace(face, this.texture.getAbsolutePath(), null, builder, world, x, y, z, renderX, renderY, renderZ);
            }
        }
    }

    public boolean shouldRender(int current, IBlockAccess blockAccess, World world, long x, long y, long z) {
        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, EnumFacing.clip(blockAccess.getBlockFacing().getNumID(), current));
        IBlockAccess near = world.getBlockAccess(pos.x(), pos.y(), pos.z());
        return !BlockPropertyDispatcher.isSolid(near);
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> textureList) {
        textureList.add(this.texture);
    }

    @Override
    public void renderBlock(IBlockAccess block, String layer, BlockAccessor region, int face, double renderX, double renderY, double renderZ, VertexBuilder builder) {

    }
}
