package net.cubecraft.client.render.block;

import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.render.BlockBakery;
import net.cubecraft.event.resource.ResourceLoadFinishEvent;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;
import me.gb2022.commons.event.EventHandler;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.Set;

import static net.cubecraft.client.render.block.ModelBlockRenderer.BLOCK_MODEL_LOAD_STAGE;

public interface IBlockRenderer {
    static void renderFace(int face, String texture, VertexBuilder builder, IWorld world, long x, long y, long z, double renderX, double renderY, double renderZ) {
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
        Vector3d faceColor = new Vector3d(1, 1, 1);
        if (face == 0) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v1), faceColor), v111, world, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v110, world, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, world, x, y, z, 0).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v1), faceColor), v011, world, x, y, z, 0).draw(builder);
            return;
        }

        if (face == 1) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, world, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v0), faceColor), v000, world, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v0), faceColor), v100, world, x, y, z, 1).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 1).draw(builder);
            return;
        }

        if (face == 2) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v0), faceColor), v011, world, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, world, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 2).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v0), faceColor), v111, world, x, y, z, 2).draw(builder);
            return;
        }


        if (face == 3) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u1, v0), faceColor), v010, world, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u0, v0), faceColor), v110, world, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u0, v1), faceColor), v100, world, x, y, z, 3).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u1, v1), faceColor), v000, world, x, y, z, 3).draw(builder);
            return;
        }

        if (face == 4) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u0, v1), faceColor), v100, world, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u1, v1), faceColor), v101, world, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u1, v0), faceColor), v111, world, x, y, z, 4).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u0, v0), faceColor), v110, world, x, y, z, 4).draw(builder);
            return;
        }

        if (face == 5) {
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u1, v0), faceColor), v011, world, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u0, v0), faceColor), v010, world, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u0, v1), faceColor), v000, world, x, y, z, 5).draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u1, v1), faceColor), v001, world, x, y, z, 5).draw(builder);
        }
    }

    void renderBlock(
            IBlockAccess blockAccess, String layer, IWorld world,
            double renderX, double renderY, double renderZ,
            VertexBuilder builder);

    default void initializeRenderer(Set<TextureAsset> textureList) {
    }


    @EventHandler
    static void onResourceReloadStart(ResourceLoadFinishEvent event){
        if(!Objects.equals(event.getStage(), BLOCK_MODEL_LOAD_STAGE)){

        }
    }
}
