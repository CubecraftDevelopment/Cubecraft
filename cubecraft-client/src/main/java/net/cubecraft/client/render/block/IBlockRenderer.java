package net.cubecraft.client.render.block;

import ink.flybird.quantum3d_legacy.draw.LegacyVertexBuilder;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import me.gb2022.commons.ColorUtil;
import me.gb2022.commons.container.Vector3;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.BlockBakery;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.CullingMethod;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import org.joml.Vector2d;
import org.joml.Vector3d;

public interface IBlockRenderer {
    static void renderFace(int face, String texture, String color, LegacyVertexBuilder builder, BlockAccessor world, long x, long y, long z, double renderX, double renderY, double renderZ) {
        Texture2DTileMap terrain = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().get("cubecraft:terrain");
        float u0 = terrain.exactTextureU(texture, 0);
        float u1 = terrain.exactTextureU(texture, 1);
        float v0 = terrain.exactTextureV(texture, 0);
        float v1 = terrain.exactTextureV(texture, 1);

        Vector3d v000 = new Vector3d(0, 0, 0);
        Vector3d v001 = new Vector3d(0, 0, 1);
        Vector3d v010 = new Vector3d(0, 1, 0);
        Vector3d v011 = new Vector3d(0, 1, 1);
        Vector3d v100 = new Vector3d(1, 0, 0);
        Vector3d v101 = new Vector3d(1, 0, 1);
        Vector3d v110 = new Vector3d(1, 1, 0);
        Vector3d v111 = new Vector3d(1, 1, 1);

        Vector3d render = new Vector3d(renderX, renderY, renderZ);

        int c;
        if (color == null || color.equals("none")) {
            c = 0xFFFFFF;
        } else {
            //todo block
            c = ClientRenderContext.COLOR_MAP.get(color).sample(world, null, x, y, z);
        }


        Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));

        if (face == 0) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v1), faceColor), v111, world, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v110, world, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, world, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v1), faceColor), v011, world, x, y, z, 0)
                    .draw(builder);
            return;
        }

        if (face == 1) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, world, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v0), faceColor), v000, world, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v0), faceColor), v100, world, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 1)
                    .draw(builder);
            return;
        }

        if (face == 2) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v0), faceColor), v011, world, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, world, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v0), faceColor), v111, world, x, y, z, 2)
                    .draw(builder);
            return;
        }


        if (face == 3) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u1, v0), faceColor), v010, world, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u0, v0), faceColor), v110, world, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u0, v1), faceColor), v100, world, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u1, v1), faceColor), v000, world, x, y, z, 3)
                    .draw(builder);
            return;
        }

        if (face == 4) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u0, v1), faceColor), v100, world, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v111, world, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u0, v0), faceColor), v110, world, x, y, z, 4)
                    .draw(builder);
            return;
        }

        if (face == 5) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u1, v0), faceColor), v011, world, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, world, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v1), faceColor), v000, world, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u1, v1), faceColor), v001, world, x, y, z, 5)
                    .draw(builder);
        }
    }

    static boolean isFaceCulled(CullingPredication culling, int face, BlockAccess block, BlockAccessor accessor) {
        if (block == null || accessor == null) {
            return true;
        }

        return culling.test(block, block.near(accessor, face, 1));
    }

    static boolean isBlockComponentVisible(CullingMethod culling, int face, BlockAccess block, BlockAccessor accessor, long x, long y, long z) {
        if (block == null || accessor == null) {
            return true;
        }

        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, face);
        BlockAccess near = accessor.getBlockAccess(pos.x(), pos.y(), pos.z());

        return switch (culling) {
            case NEVER -> false;
            case ALWAYS -> true;
            case SOLID -> !BlockPropertyDispatcher.isSolid(near);
            case SOLID_OR_EQUALS -> BlockPropertyDispatcher.isSolid(near) || block.getBlockId() == near.getBlockId();
            case EQUALS -> block.getBlockId() == near.getBlockId();
        };
    }

    default void provideTileMapItems(MultiAssetContainer<TextureAsset> list){}

    void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder);
}
