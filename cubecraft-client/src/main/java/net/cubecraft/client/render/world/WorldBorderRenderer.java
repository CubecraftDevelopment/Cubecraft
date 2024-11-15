package net.cubecraft.client.render.world;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.legacy.drawcall.IRenderCall;
import me.gb2022.quantum3d.legacy.drawcall.ListRenderCall;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.world.environment.Environment;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.util.ShapeRenderer;
import org.lwjgl.opengl.GL11;

@TypeItem("cubecraft:world_border")
public final class WorldBorderRenderer extends IWorldRenderer {
    private static final int COLOR = 0x3366cc;

    private IRenderCall border;

    @Override
    public void stop() {
        if (this.border == null) {
            return;
        }
        if (!this.border.isAllocated()) {
            return;
        }
        this.border.free();
    }

    @Override
    public void init() {
        this.border = new ListRenderCall();
        this.border.allocate();

        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(128);

        builder.color(0xdc, 0xe9, 0xf5, 0xaa);

        double d = Environment.VALID_WORLD_RADIUS;

        ShapeRenderer.renderAABBBoxInner(builder, new AABB(-d, -d, -d, d, d, d));
        this.border.upload(builder);
        builder.free();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }

        GL11.glPushMatrix();
        this.camera.setupGlobalTranslate();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.border.call();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
