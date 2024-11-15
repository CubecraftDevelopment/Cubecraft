package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.legacy.draw.DrawMode;
import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.util.ShapeRenderer;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.chunk.pos.ChunkPos;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.HUD)
public class HUDRenderer extends IWorldRenderer {
    public boolean allowDebug = true;

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.TRANSPARENT) {
            return;
        }
        this.setGlobalCamera(delta);

        this.renderSelectionBox();
        if (ClientSharedContext.getClient().isDebug && this.allowDebug) {
            GL11.glLineWidth(1.0f);
            this.renderChunkBorder();
            this.renderEntityBoundary();
        }
    }

    private void renderSelectionBox() {
        if (this.player.hitResult == null || !(this.player.hitResult.instanceOf(IBlockAccess.class))) {
            return;
        }

        GL11.glPushMatrix();
        HitBox aabb = this.player.hitResult.getHitBox();
        BlockAccess sel = this.player.hitResult.getObject(IBlockAccess.class);
        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(64, DrawMode.LINES);
        builder.begin();
        this.camera.setupObjectCamera(aabb.minPos());
        builder.color(0.1f, 0.1f, 0.1f, 1.0f);
        ShapeRenderer.renderAABB(builder, new AABB(-0.01, -0.01, -0.01, 1.01, 1.01, 1.01));

        builder.end();
        builder.uploadPointer();
        builder.free();
        GL11.glPopMatrix();
    }

    private void renderChunkBorder() {
        GL11.glPushMatrix();

        long cx = (long) ((Math.floor(this.camera.getPosition().x) + 0)) >> 4;
        long cz = (long) ((Math.floor(this.camera.getPosition().z) + 0)) >> 4;
        long cy = (long) ((Math.floor(this.camera.getPosition().y) + 0)) >> 4;

        this.camera.setupObjectCamera(new Vector3d(cx * 16, 0, cz * 16));
        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(65536, DrawMode.LINES);
        builder.begin();
        builder.color(255, 251, 13);
        ShapeRenderer.renderAABB(builder, new AABB(0, 0, 0, 16, 512, 16));
        for (int y = 0; y < 32; y++) {
            ShapeRenderer.renderAABB(builder, new AABB(0, y * 16, 0, 16, y * 16 + 16, 16));
        }

        builder.color(1, 0, 0, 0.5f);
        ShapeRenderer.renderAABB(builder, new AABB(-16, 0, -16, 32, 512, 32));

        builder.end();
        builder.uploadPointer();
        builder.free();
        GL11.glPopMatrix();

        cx *= 16;
        cy *= 16;
        cz *= 16;

        LegacyVertexBuilder builder2 = VertexBuilderAllocator.createByPrefer(128, DrawMode.LINES);
        builder2.begin();
        builder2.color(255, 251, 13);

        AABB aabb = new AABB(cx + 7, cy + 7, cz + 7, cx + 9, cy + 9, cz + 9);

        ShapeRenderer.renderAABB(builder2, aabb);

        GL11.glPushMatrix();
        this.camera.setupObjectCamera(new Vector3d(0, 0, 0));

        builder2.end();
        builder2.uploadPointer();
        builder2.free();

        GL11.glPopMatrix();
    }

    private void renderEntityBoundary() {

    }

    @Override
    public void config(JsonObject json) {
        this.allowDebug = json.get("allow_debug").getAsBoolean();
    }
}
