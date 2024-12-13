package net.cubecraft.client.render.model.block.component;

import com.google.gson.*;
import me.gb2022.commons.ColorUtil;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.texture.Texture2DTileMap;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.render.block.BlockBakery;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.Objects;

public final class Cube extends BlockModelComponent {
    private final BlockModelFace top;
    private final BlockModelFace bottom;
    private final BlockModelFace left;
    private final BlockModelFace right;
    private final BlockModelFace front;
    private final BlockModelFace back;

    public Cube(String layer, Vector3f start, Vector3f end, BlockModelFace top1, BlockModelFace bottom1, BlockModelFace left1, BlockModelFace right1, BlockModelFace front1, BlockModelFace back1) {
        super(layer, start, end);
        this.top = top1;
        this.bottom = bottom1;
        this.left = left1;
        this.right = right1;
        this.front = front1;
        this.back = back1;
    }

    public Cube(JsonObject json) {
        super(json.get("layer").getAsString(), new Vector3f(json.get("start").getAsJsonArray().get(0).getAsFloat(),
                                                            json.get("start").getAsJsonArray().get(1).getAsFloat(),
                                                            json.get("start").getAsJsonArray().get(2).getAsFloat()
        ), new Vector3f(json.get("end").getAsJsonArray().get(0).getAsFloat(),
                        json.get("end").getAsJsonArray().get(1).getAsFloat(),
                        json.get("end").getAsJsonArray().get(2).getAsFloat()
        ));

        var faces = json.getAsJsonObject("faces");

        this.top = faces.has("top") ? new BlockModelFace(faces.getAsJsonObject("top")) : null;
        this.bottom = faces.has("bottom") ? new BlockModelFace(faces.getAsJsonObject("bottom")) : null;
        this.left = faces.has("left") ? new BlockModelFace(faces.getAsJsonObject("left")) : null;
        this.right = faces.has("right") ? new BlockModelFace(faces.getAsJsonObject("right")) : null;
        this.front = faces.has("front") ? new BlockModelFace(faces.getAsJsonObject("front")) : null;
        this.back = faces.has("back") ? new BlockModelFace(faces.getAsJsonObject("back")) : null;
    }


    @Override
    public void render(VertexBuilder builder, Registered<ChunkLayerContainerFactory.Provider> layer, int face, BlockAccessor world, BlockAccess block, double renderX, double renderY, double renderZ) {
        if (this.localizedLayer != layer.getId()) {
            return;
        }
        if (face == 6) {
            return;
        }

        var f = getFace(face);

        if (f == null) {
            return;
        }

        if (IBlockRenderer.isFaceCulled(f.culling(), face, block, world)) {
            return;
        }

        this.renderFace(f, layer, face, builder, world, block, renderX, renderY, renderZ);
    }

    @Override
    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
        var textureList = list.getChannel(this.layer);

        if (this.top != null) {
            textureList.add(this.top.texture());
        }
        if (this.bottom != null) {
            textureList.add(this.bottom.texture());
        }
        if (this.left != null) {
            textureList.add(this.left.texture());
        }
        if (this.right != null) {
            textureList.add(this.right.texture());
        }
        if (this.front != null) {
            textureList.add(this.front.texture());
        }
        if (this.back != null) {
            textureList.add(this.back.texture());
        }
    }

    //face
    public BlockModelFace getFace(int id) {
        return switch (id) {
            case 0 -> top;
            case 1 -> bottom;
            case 2 -> left;
            case 3 -> right;
            case 4 -> front;
            case 5 -> back;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public void renderFace(BlockModelFace f, Registered<ChunkLayerContainerFactory.Provider> layer, int face, VertexBuilder builder, BlockAccessor w, BlockAccess block, double renderX, double renderY, double renderZ) {
        Texture2DTileMap terrain = layer.get().getTextureUsed();

        String path = f.texture().getAbsolutePath();

        var x = block != null ? block.getX() : 0;
        var y = block != null ? block.getY() : 0;
        var z = block != null ? block.getZ() : 0;


        var u0 = terrain.exactTextureU(path, f.u0());
        var u1 = terrain.exactTextureU(path, f.u1());
        var v0 = terrain.exactTextureV(path, f.v0());
        var v1 = terrain.exactTextureV(path, f.v1());

        var v000 = new Vector3f(this.start.x, this.start.y, this.start.z);
        var v001 = new Vector3f(this.start.x, this.start.y, this.end.z);
        var v010 = new Vector3f(this.start.x, this.end.y, this.start.z);
        var v011 = new Vector3f(this.start.x, this.end.y, this.end.z);
        var v100 = new Vector3f(this.end.x, this.start.y, this.start.z);
        var v101 = new Vector3f(this.end.x, this.start.y, this.end.z);
        var v110 = new Vector3f(this.end.x, this.end.y, this.start.z);
        var v111 = new Vector3f(this.end.x, this.end.y, this.end.z);

        var render = new Vector3f((float) renderX, (float) renderY, (float) renderZ);
        int c = ClientRenderContext.COLOR_MAP.get(f.color()).sample(w, block);
        if (face == 0) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v111).add(render), new Vector2d(u1, v1), faceColor), v111, w, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v110).add(render), new Vector2d(u1, v0), faceColor), v110, w, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v010).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 0)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v011).add(render), new Vector2d(u0, v1), faceColor), v011, w, x, y, z, 0)
                    .draw(builder);
            return;
        }

        if (face == 1) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v001).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v000).add(render), new Vector2d(u0, v0), faceColor), v000, w, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v100).add(render), new Vector2d(u1, v0), faceColor), v100, w, x, y, z, 1)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v101).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 1)
                    .draw(builder);
            return;
        }

        if (face == 2) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v011).add(render), new Vector2d(u0, v0), faceColor), v011, w, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v001).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v101).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 2)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v111).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 2)
                    .draw(builder);
            return;
        }


        if (face == 3) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v010).add(render), new Vector2d(u1, v0), faceColor), v010, w, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v110).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v100).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 3)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v000).add(render), new Vector2d(u1, v1), faceColor), v000, w, x, y, z, 3)
                    .draw(builder);
            return;
        }

        if (face == 4) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v101).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v100).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v110).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 4)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v111).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 4)
                    .draw(builder);
            return;
        }

        if (face == 5) {
            var faceColor = new Vector3f(ColorUtil.int1ToFloat3(c));
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v011).add(render), new Vector2d(u1, v0), faceColor), v011, w, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v010).add(render), new Vector2d(u0, v0), faceColor), v010, w, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v000).add(render), new Vector2d(u0, v1), faceColor), v000, w, x, y, z, 5)
                    .draw(builder);
            BlockBakery.bakeVertex(Vertex.create(new Vector3f(v001).add(render), new Vector2d(u1, v1), faceColor), v001, w, x, y, z, 5)
                    .draw(builder);
        }
    }


    public static class JDeserializer implements JsonDeserializer<BlockModelComponent> {
        @Override
        public BlockModelComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (Objects.equals(jsonElement.getAsJsonObject().get("type").getAsString(), "cube")) {
                return new net.cubecraft.client.render.model.block.component.Cube(jsonElement.getAsJsonObject());
            }
            return null;
        }
    }
}
