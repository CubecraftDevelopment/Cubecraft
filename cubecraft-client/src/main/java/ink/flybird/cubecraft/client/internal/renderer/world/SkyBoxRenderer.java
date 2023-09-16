package ink.flybird.cubecraft.client.internal.renderer.world;

import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.quantum3d_legacy.drawcall.VBORenderCall;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.SKY_BOX)
public final class SkyBoxRenderer extends IWorldRenderer {
    private final IRenderCall sky = new VBORenderCall();
    private final FrustumCuller frustum = new FrustumCuller(this.camera);
    float MOD = 1;

    public SkyBoxRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
        this.sky.allocate();
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }


        this.camera.setUpGlobalCamera(this.window);
        this.frustum.calculateFrustum();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_FOG);
        this.sky.call();
        GL11.glEnable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void refresh() {
        int d2 = this.setting.getValueAsInt("client.render.terrain.render_distance", 4);
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
            builder.color(0x33 / 255f * MOD, 0x9b / 255f * MOD, 0xe8 / 255f * MOD, 1.0f);
            builder.vertex(cx, cy + d2 * 64, cz);
            builder.color(0xdc / 255f * MOD, 0xe9 / 255f * MOD, 0xf5 / 255f * MOD, 1.0f);
            builder.vertex(x, cy, y);
            builder.vertex(x1, cy, y1);
        }
        builder.end();
        this.sky.upload(builder);
        builder.free();
    }
}
