package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import ink.flybird.fcommon.registry.TypeItem;
import me.gb2022.quantum3d.ColorElement;
import me.gb2022.quantum3d.lwjgl.batching.GLRenderList;
import me.gb2022.quantum3d.render.vertex.*;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.SKY_BOX)
public final class SkyBoxRenderer extends IWorldRenderer {
    private static final VertexBuilderAllocator ALLOCATOR = new VertexBuilderAllocator(LevelRenderer.ALLOCATOR);
    private final GLRenderList skyRenderBatch = new GLRenderList();

    private ColorElement skyColor;
    private ColorElement skyFogColor;

    @Override
    public void stop() {
        this.skyRenderBatch.free();
    }

    @Override
    public void init() {
        this.build();
    }

    public void build() {
        this.skyRenderBatch.allocate();
        int d2 = ClientSettingRegistry.getFixedViewDistance();
        VertexBuilder builder = ALLOCATOR.allocate(VertexFormat.V3F_C3F, DrawMode.TRIANGLES, 5120);
        builder.allocate();

        float cx = 0;
        float cy = d2 * 16 * 2;
        float cz = 0;
        float r = d2 * 16 * 32;

        for (int i = 0; i < 360; ++i) {
            float x = (float) Math.cos((double) i * Math.PI / 180) * r + cx;
            float y = (float) Math.sin((double) i * Math.PI / 180) * r + cy;

            float x1 = (float) Math.cos((double) (i + 1) * Math.PI / 180) * r + cx;
            float y1 = (float) Math.sin((double) (i + 1) * Math.PI / 180) * r + cy;
            builder.setColor(this.skyColor.RGB_F());
            builder.addVertex(cx, cy + d2 * 64, cz);
            builder.setColor(this.skyFogColor.RGB_F());
            builder.addVertex(x, cy, y);
            builder.addVertex(x1, cy, y1);

            builder.setColor(this.skyColor.RGB_F());
            builder.addVertex(cx, -(cy + d2 * 64), cz);
            builder.setColor(this.skyFogColor.RGB_F());
            builder.addVertex(x, -cy, y);
            builder.addVertex(x1, -cy, y1);
        }

        VertexBuilder builder2 = ALLOCATOR.allocate(VertexFormat.V3F_C3F, DrawMode.QUAD_STRIP, 5120);
        builder2.allocate();

        builder2.setColor(this.skyFogColor.RGB_F());
        int sides = 32; // 设置圆柱体的边数
        double radius = d2 * 16 * 32; // 设置圆柱体的半径
        double height = d2 * 64; // 设置圆柱体的高度

        for (int i = 0; i <= sides; i++) {
            double angle = Math.PI * 2 * i / sides;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            builder2.addVertex(x, height, z); // 上圆柱体的点
            builder2.addVertex(x, -height, z); // 下圆柱体的点
        }

        this.skyRenderBatch.upload(() -> {
            VertexBuilderUploader.uploadPointer(builder);
            VertexBuilderUploader.uploadPointer(builder2);
        });

        ALLOCATOR.free(builder);
        ALLOCATOR.free(builder2);
    }

    @Override
    public void preRender(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }
        this.camera.setUpGlobalCamera();
    }

    @Override
    public void render(RenderType type, float delta) {
        GL11.glClearColor(1, 1, 1, 1);

        if (type != RenderType.ALPHA) {
            return;
        }
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.skyRenderBatch.call();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_FOG);
    }

    @Override
    public void config(JsonObject json) {
        this.skyColor = ColorElement.parseFromString(json.get("sky_color").getAsString());
        this.skyFogColor = ColorElement.parseFromString(json.get("sky_fog_color").getAsString());
    }


}
