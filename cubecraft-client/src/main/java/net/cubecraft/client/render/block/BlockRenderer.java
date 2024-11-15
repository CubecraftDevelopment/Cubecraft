package net.cubecraft.client.render.block;

import com.google.gson.JsonObject;
import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.registry.RegisteredConstructor;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.item.IResource;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;

import java.util.Collection;
import java.util.List;

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

    public boolean shouldRender(int current, BlockAccess blockAccess, World world, long x, long y, long z) {
        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, current);
        BlockAccess near = world.getBlockAccess(pos.x(), pos.y(), pos.z());
        return !BlockPropertyDispatcher.isSolid(near);
    }

    @Override
    public void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder) {

    }
}
