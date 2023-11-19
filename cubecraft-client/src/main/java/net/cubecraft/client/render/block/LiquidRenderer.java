package net.cubecraft.client.render.block;

import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.render.BlockBakery;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import ink.flybird.fcommon.ColorUtil;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.registry.TypeItem;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.Set;

@TypeItem("cubecraft:liquid")
public final class LiquidRenderer implements IBlockRenderer {
    private final TextureAsset calmTexture;
    private final TextureAsset flowTexture;

    public LiquidRenderer(TextureAsset calmTexture, TextureAsset flowTexture) {
        this.calmTexture = calmTexture;
        this.flowTexture = flowTexture;
    }

    @Override
    public void renderBlock(IBlockAccess blockAccess, String layer, IWorld world, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        long x = blockAccess.getX();
        long y = blockAccess.getY();
        long z = blockAccess.getZ();
        if (!Objects.equals(layer, "cubecraft:transparent_block")) {
            return;
        }
        for (int face = 0; face < 6; face++) {
            if (this.shouldRender(face, blockAccess, world, x, y, z)) {
                this.renderFace(face, builder, world, blockAccess, x, y, z, renderX, renderY, renderZ);
            }
        }
    }

    public boolean shouldRender(int current, IBlockAccess blockAccess, IWorld world, long x, long y, long z) {
        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, current);
        IBlockAccess near = world.getBlockAccess(pos.x(), pos.y(), pos.z());
        boolean nearSolid = BlockPropertyDispatcher.isSolid(near);
        boolean nearEquals = Objects.equals(near.getBlockID(), blockAccess.getBlockID());
        if(current==0&&!nearEquals){
            return true;
        }
        return !(nearSolid || nearEquals);
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> textureList) {
        textureList.add(this.flowTexture);
        textureList.add(this.calmTexture);
    }

    public void renderFace(int face, VertexBuilder builder, IWorld w, IBlockAccess blockAccess, long x, long y, long z, double renderX, double renderY, double renderZ) {
        Texture2DTileMap terrain = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().get("cubecraft:terrain");

        String path = face==0||face==1? this.calmTexture.getAbsolutePath():this.flowTexture.getAbsolutePath();

        float u0 = terrain.exactTextureU(path, 0);
        float u1 = terrain.exactTextureU(path, 0.5f);
        float v0 = terrain.exactTextureV(path, 0);
        float v1 = terrain.exactTextureV(path, 1 / 32f);

        if (face == 0 || face == 1) {
            u0 = terrain.exactTextureU(path, 0);
            u1 = terrain.exactTextureU(path, 1);
            v0 = terrain.exactTextureV(path, 0);
            v1 = terrain.exactTextureV(path, 1 / 32f);
        }

        Vector3d v000 = new Vector3d(0, 0, 0);
        Vector3d v001 = new Vector3d(0, 0, 1);
        Vector3d v010 = new Vector3d(0, 0.875, 0);
        Vector3d v011 = new Vector3d(0, 0.875, 1);
        Vector3d v100 = new Vector3d(1, 0, 0);
        Vector3d v101 = new Vector3d(1, 0, 1);
        Vector3d v110 = new Vector3d(1, 0.875, 0);
        Vector3d v111 = new Vector3d(1, 0.875, 1);

        Vector3d render = new Vector3d(renderX, renderY, renderZ);
        int c = 0x3F76E4;
        if (face == 0) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));

            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v000, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v100, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 0).draw(builder);

            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v1), faceColor), v111, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v110, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v1), faceColor), v011, w, x, y, z, 0).draw(builder);
            return;
        }
        if (face == 1) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v0), faceColor), v000, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v0), faceColor), v100, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 1).draw(builder);

            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v111, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v0), faceColor), v110, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v011, w, x, y, z, 1).draw(builder);
            return;
        }
        if (face == 2) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u1, v0), faceColor), v010, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u1, v1), faceColor), v000, w, x, y, z, 3).draw(builder);

            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v0), faceColor), v011, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 2).draw(builder);
            return;
        }
        if (face == 3) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v011, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 2).draw(builder);

            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u1, v0), faceColor), v010, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u1, v1), faceColor), v000, w, x, y, z, 3).draw(builder);
            return;
        }
        if (face == 4) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v0), faceColor), v011, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u0, v1), faceColor), v000, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v001, w, x, y, z, 5).draw(builder);


            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 4).draw(builder);
            return;
        }
        if (face == 5) {
            Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u1, v0), faceColor), v011, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v1), faceColor), v000, w, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u1, v1), faceColor), v001, w, x, y, z, 5).draw(builder);
        }
    }
}
