package net.cubecraft.client.render.world;

import ink.flybird.fcommon.registry.TypeItem;
import me.gb2022.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.quantum3d_legacy.drawcall.ListRenderCall;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.SKY_BOX)
public final class SkyBoxRenderer extends IWorldRenderer {
    public static final int FOG = 0xdce9f5;
    private static final int SKY = 0x79a7ff;

    private final IRenderCall skyCover = new ListRenderCall();
    private final IRenderCall skySide = new ListRenderCall();

    public SkyBoxRenderer(Window window, IWorld world, EntityPlayer player, Camera cam) {
        super(window, world, player, cam);
        if (!this.skyCover.isAllocated()) {
            this.skyCover.allocate();
            this.skySide.allocate();
        }
    }

    @Override
    public void stop() {
        if (!this.skyCover.isAllocated()) {
            return;
        }
        this.skyCover.free();
        this.skySide.free();
    }

    @Override
    public void init() {
        this.build();
    }

    @Override
    public void refresh() {
        this.build();
    }

    public void build() {
        if (!this.skyCover.isAllocated()) {
            this.skyCover.allocate();
            this.skySide.allocate();
        }
        int d2 = ClientSettingRegistry.getFixedViewDistance();
        VertexBuilder builder = new OffHeapVertexBuilder(4096, DrawMode.TRIANGLES);
        builder.begin();

        float cx = 0;
        float cy = d2 * 16 * 2;
        float cz = 0;
        float r = d2 * 16 * 32;

        for (int i = 0; i < 360; ++i) {
            float x = (float) Math.cos((double) i * Math.PI / 180) * r + cx;
            float y = (float) Math.sin((double) i * Math.PI / 180) * r + cy;

            float x1 = (float) Math.cos((double) (i + 1) * Math.PI / 180) * r + cx;
            float y1 = (float) Math.sin((double) (i + 1) * Math.PI / 180) * r + cy;
            builder.color(SKY);
            builder.vertex(cx, cy + d2 * 64, cz);
            builder.color(FOG);
            builder.vertex(x, cy, y);
            builder.vertex(x1, cy, y1);

            builder.color(0x2255cc);
            builder.vertex(cx, -(cy + d2 * 64), cz);
            builder.color(FOG);
            builder.vertex(x, -cy, y);
            builder.vertex(x1, -cy, y1);
        }
        builder.end();
        this.skyCover.upload(builder);
        builder.free();

        builder = VertexBuilderAllocator.createByPrefer(4096, DrawMode.QUAD_STRIP);

        builder.begin();
        builder.color(FOG);
        int sides = 32; // 设置圆柱体的边数
        double radius = d2 * 16 * 32; // 设置圆柱体的半径
        double height = d2 * 64; // 设置圆柱体的高度

        for (int i = 0; i <= sides; i++) {
            double angle = Math.PI * 2 * i / sides;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            builder.vertex(x, height, z); // 上圆柱体的点
            builder.vertex(x, -height, z); // 下圆柱体的点
        }

        builder.end();
        this.skySide.upload(builder);
        builder.free();
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
        if (type != RenderType.ALPHA) {
            return;
        }

        /*
        GL11.glPushMatrix();
        GL11.glTranslatef(0, (float) -this.camera.getPosition().y, 0);
        int r = ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16 * 100;
        int x0 = -r, x1 = r, y1 = 0, z0 = -r, z1 = r;
        VertexBuilder builder = new OffHeapVertexBuilder(16, DrawMode.QUADS);
        builder.begin();
        builder.color(0x3366cc);
        builder.vertex(x1, y1, z1);
        builder.vertex(x1, y1, z0);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y1, z1);
        builder.end();
        GLUtil.disableBlend();
        GL11.glDisable(GL11.GL_CULL_FACE);
        builder.uploadPointer();
        builder.free();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GLUtil.enableBlend();
        GL11.glPopMatrix();

         */

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.skyCover.call();
        this.skySide.call();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
