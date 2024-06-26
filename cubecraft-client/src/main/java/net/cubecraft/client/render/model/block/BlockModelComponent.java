package net.cubecraft.client.render.model.block;

import com.google.gson.*;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;
import me.gb2022.commons.ColorUtil;
import me.gb2022.commons.container.Vector3;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.BlockBakery;
import net.cubecraft.client.render.model.CullingMethod;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public abstract class BlockModelComponent {
    protected final String layer;
    protected final Vector3d start, end;

    protected BlockModelComponent(String layer, Vector3d start, Vector3d end) {
        this.layer = layer;
        this.start = start;
        this.end = end;
    }


    //model
    public String getRenderLayer() {
        return this.layer;
    }

    public Vector3d getStart() {
        return start;
    }

    public Vector3d getEnd() {
        return end;
    }


    //abstract method
    public abstract void render(VertexBuilder builder, String layer, IWorld world, IBlockAccess blockAccess, double renderX, double renderY, double renderZ);

    public abstract void renderAsItem(VertexBuilder builder, double renderX, double renderY, double renderZ);

    public abstract void initializeModel(Set<TextureAsset> textureList);

    public static class Cube extends BlockModelComponent {
        private final BlockModelFace top;
        private final BlockModelFace bottom;
        private final BlockModelFace left;
        private final BlockModelFace right;
        private final BlockModelFace front;
        private final BlockModelFace back;

        public Cube(String layer, Vector3d start, Vector3d end, BlockModelFace top1, BlockModelFace bottom1, BlockModelFace left1, BlockModelFace right1, BlockModelFace front1, BlockModelFace back1) {
            super(layer, start, end);
            this.top = top1;
            this.bottom = bottom1;
            this.left = left1;
            this.right = right1;
            this.front = front1;
            this.back = back1;
        }

        public Cube(JsonObject json) {
            this(
                    json.get("layer").getAsString(),
                    new Vector3d(
                            json.get("start").getAsJsonArray().get(0).getAsDouble(),
                            json.get("start").getAsJsonArray().get(1).getAsDouble(),
                            json.get("start").getAsJsonArray().get(2).getAsDouble()
                    ),
                    new Vector3d(
                            json.get("end").getAsJsonArray().get(0).getAsDouble(),
                            json.get("end").getAsJsonArray().get(1).getAsDouble(),
                            json.get("end").getAsJsonArray().get(2).getAsDouble()
                    ),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("top").getAsJsonObject()),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("bottom").getAsJsonObject()),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("left").getAsJsonObject()),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("right").getAsJsonObject()),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("front").getAsJsonObject()),
                    new BlockModelFace(json.get("faces").getAsJsonObject().get("back").getAsJsonObject())
            );
        }


        //implement
        @Override
        public void render(VertexBuilder builder, String layer, IWorld world, IBlockAccess blockAccess, double renderX, double renderY, double renderZ) {
            long x = blockAccess.getX();
            long y = blockAccess.getY();
            long z = blockAccess.getZ();

            if (!Objects.equals(layer, this.getRenderLayer())) {
                return;
            }
            for (int f = 0; f < 6; f++) {
                if (this.shouldRender(f, blockAccess, world, x, y, z)) {
                    this.renderFace(this.getFace(f), f, builder, world, blockAccess, x, y, z, renderX, renderY, renderZ);
                }
            }
        }

        @Override
        public void renderAsItem(VertexBuilder builder, double renderX, double renderY, double renderZ) {
            //todo:物品渲染
        }

        @Override
        public void initializeModel(Set<TextureAsset> textureList) {
            textureList.add(this.top.texture());
            textureList.add(this.bottom.texture());
            textureList.add(this.left.texture());
            textureList.add(this.right.texture());
            textureList.add(this.front.texture());
            textureList.add(this.back.texture());
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

        public boolean shouldRender(int f, IBlockAccess blockAccess, IWorld world, long x, long y, long z) {
            BlockModelFace face = this.getFace(f);
            Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, f);
            return switch (face.culling()) {
                case NEVER -> false;
                case SOLID -> !BlockPropertyDispatcher.isSolid(world.getBlockAccess(pos.x(), pos.y(), pos.z()));
                case ALWAYS -> true;
                case SOLID_OR_EQUALS -> !BlockPropertyDispatcher.isSolid(world.getBlockAccess(pos.x(), pos.y(), pos.z())) &&
                        !(Objects.equals(world.getBlockAccess(pos.x(), pos.y(), pos.z()).getBlockID(), blockAccess.getBlockID()));
                case EQUALS ->
                        !(Objects.equals(world.getBlockAccess(pos.x(), pos.y(), pos.z()).getBlockID(), blockAccess.getBlockID()));
            };
        }

        public void renderFace(BlockModelFace f, int face, VertexBuilder builder, IWorld w, IBlockAccess blockAccess, long x, long y, long z, double renderX, double renderY, double renderZ) {
            Texture2DTileMap terrain = net.cubecraft.client.context.ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().get("cubecraft:terrain");

            String path = f.texture().getAbsolutePath();
            float u0 = terrain.exactTextureU(path, f.u0());
            float u1 = terrain.exactTextureU(path, f.u1());
            float v0 = terrain.exactTextureV(path, f.v0());
            float v1 = terrain.exactTextureV(path, f.v1());

            Vector3d v000 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.start.x, this.start.y, this.start.z));
            Vector3d v001 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.start.x, this.start.y, this.end.z));
            Vector3d v010 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.start.x, this.end.y, this.start.z));
            Vector3d v011 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.start.x, this.end.y, this.end.z));
            Vector3d v100 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.end.x, this.start.y, this.start.z));
            Vector3d v101 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.end.x, this.start.y, this.end.z));
            Vector3d v110 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.end.x, this.end.y, this.start.z));
            Vector3d v111 = blockAccess.getBlockFacing().clipVec(new Vector3d(this.end.x, this.end.y, this.end.z));

            Vector3d render = new Vector3d(renderX, renderY, renderZ);
            int c = ClientRenderContext.COLOR_MAP.get(f.color()).sample(w, blockAccess, x, y, z);
            if (face == 0) {
                Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
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
                return;
            }

            if (face == 2) {
                Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v011).add(render), new Vector2d(u0, v0), faceColor), v011, w, x, y, z, 2).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v001).add(render), new Vector2d(u0, v1), faceColor), v001, w, x, y, z, 2).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v101).add(render), new Vector2d(u1, v1), faceColor), v101, w, x, y, z, 2).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v111).add(render), new Vector2d(u1, v0), faceColor), v111, w, x, y, z, 2).draw(builder);
                return;
            }


            if (face == 3) {
                Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v010).add(render), new Vector2d(u1, v0), faceColor), v010, w, x, y, z, 3).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v110).add(render), new Vector2d(u0, v0), faceColor), v110, w, x, y, z, 3).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v100).add(render), new Vector2d(u0, v1), faceColor), v100, w, x, y, z, 3).draw(builder);
                BlockBakery.bakeVertex(Vertex.create(new Vector3d(v000).add(render), new Vector2d(u1, v1), faceColor), v000, w, x, y, z, 3).draw(builder);
                return;
            }

            if (face == 4) {
                Vector3d faceColor = new Vector3d(ColorUtil.int1ToFloat3(c));
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


        public static class JDeserializer implements JsonDeserializer<BlockModelComponent> {
            @Override
            public BlockModelComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (Objects.equals(jsonElement.getAsJsonObject().get("type").getAsString(), "cube")) {
                    return new Cube(jsonElement.getAsJsonObject());
                }
                return null;
            }
        }
    }

    public static record BlockModelFace(TextureAsset texture, float u0, float u1, float v0, float v1, String color,
                                        CullingMethod culling) {

        public BlockModelFace(JsonObject json) {
            this(
                    new TextureAsset(json.get("texture").getAsString()),
                    json.get("uv").getAsJsonArray().get(0).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(1).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(2).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(3).getAsFloat(),
                    json.has("color") ? json.get("color").getAsString() : "cubecraft:default",
                    CullingMethod.from(json.has("culling") ? json.get("culling").getAsString() : "solid")
            );
        }
    }
}