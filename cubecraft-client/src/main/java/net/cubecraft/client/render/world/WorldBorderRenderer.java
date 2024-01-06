package net.cubecraft.client.render.world;

import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.dimension.Dimension;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;
import me.gb2022.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.quantum3d_legacy.drawcall.ListRenderCall;
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

        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(128);

        builder.color(0xdc, 0xe9, 0xf5, 0xaa);

        double d = Dimension.VALID_WORLD_RADIUS;

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
