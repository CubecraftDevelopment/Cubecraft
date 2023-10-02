package ink.flybird.cubecraft.client.render.world;

import ink.flybird.cubecraft.client.internal.renderer.world.WorldRendererType;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.quantum3d_legacy.drawcall.ListRenderCall;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.SKY_BOX)
public final class SkyBoxRenderer extends IWorldRenderer {
    private final IRenderCall sky = new ListRenderCall();
    private final IRenderCall sky2 = new ListRenderCall();

    public SkyBoxRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
    }

    @Override
    public void stop() {
        if(!this.sky.isAllocated()){
            return;
        }
        this.sky.free();
        this.sky2.free();
    }

    @Override
    public void init() {
        this.sky.allocate();
        this.sky2.allocate();


        int d2 = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue();
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(4096, DrawMode.TRIANGLES);
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
            builder.color(0x33 / 255f, 0x9b / 255f, 0xe8 / 255f, 1.0f);
            builder.vertex(cx, cy + d2 * 64, cz);
            builder.color(0xdc / 255f, 0xe9 / 255f, 0xf5 / 255f, 1.0f);
            builder.vertex(x, cy, y);
            builder.vertex(x1, cy, y1);

            builder.color(0xdc / 255f, 0xe9 / 255f, 0xf5 / 255f, 1.0f);
            builder.vertex(cx, -(cy + d2 * 64), cz);
            builder.vertex(x, -cy, y);
            builder.vertex(x1, -cy, y1);
        }
        builder.end();
        this.sky.upload(builder);
        builder.free();

        builder = VertexBuilderAllocator.createByPrefer(4096, DrawMode.QUAD_STRIP);

        builder.begin();
        builder.color(0xdc / 255f, 0xe9 / 255f, 0xf5 / 255f, 1.0f);
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

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        builder.end();
        this.sky2.upload(builder);
        builder.free();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }
        this.camera.setUpGlobalCamera(this.window);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.sky.call();
        this.sky2.call();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
