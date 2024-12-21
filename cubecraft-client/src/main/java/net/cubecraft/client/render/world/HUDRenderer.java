package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.legacy.draw.DrawMode;
import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import me.gb2022.quantum3d.util.ShapeRenderer;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
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

        GL11.glLineWidth(3.0f);

        this.renderSelectionBox();
        if (CubecraftClient.getInstance().isDebug && this.allowDebug) {

            this.renderChunkBorder();
            this.renderEntityBoundary(delta);
        }
    }

    private void renderSelectionBox() {
        if (this.player.hitResult == null || !(this.player.hitResult.instanceOf(IBlockAccess.class))) {
            return;
        }

        HitBox aabb = this.player.hitResult.getHitBox();
        BlockAccess sel = this.player.hitResult.getObject(IBlockAccess.class);
        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(64, DrawMode.LINES);
        builder.begin();
        builder.color(0.1f, 0.1f, 0.1f, 1.0f);


        var vx = Math.abs(this.viewCamera.getX() - sel.getX()-0.3);
        var vy = Math.abs(this.viewCamera.getY() - sel.getY()-0.3);
        var vz = Math.abs(this.viewCamera.getZ() - sel.getZ()-0.3);

        var mul = 0.002f;

        ShapeRenderer.renderAABB(builder, new AABB(0, 0, 0, 1, 1, 1).grow(vx * mul, vy * mul, vz * mul));

        this.viewCamera.push().object(aabb.minPos()).set();

        builder.end();
        builder.uploadPointer();
        builder.free();

        this.viewCamera.pop();
    }

    private void renderChunkBorder() {
        long cx = (long) ((Math.floor(this.viewCamera.getX()) + 0)) >> 4;
        long cy = (long) ((Math.floor(this.viewCamera.getY()) + 0)) >> 4;
        long cz = (long) ((Math.floor(this.viewCamera.getZ()) + 0)) >> 4;

        this.viewCamera.push().object(new Vector3d(cx * 16, 0, cz * 16)).set();
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

        this.viewCamera.pop();

        cx *= 16;
        cy *= 16;
        cz *= 16;

        LegacyVertexBuilder builder2 = VertexBuilderAllocator.createByPrefer(128, DrawMode.LINES);
        builder2.begin();
        builder2.color(255, 251, 13);

        AABB aabb = new AABB(cx + 7, cy + 7, cz + 7, cx + 9, cy + 9, cz + 9);

        ShapeRenderer.renderAABB(builder2, aabb);

        this.viewCamera.push().object(new Vector3d(0, 0, 0)).set();

        builder2.end();
        builder2.uploadPointer();
        builder2.free();

        this.viewCamera.pop();
    }

    private void renderEntityBoundary(float delta) {
        var builder = this.getVertexBuilderAllocator()
                .allocate(VertexFormat.V3F_C3F, me.gb2022.quantum3d.render.vertex.DrawMode.LINES, 128);


        for (var entity : this.world.getEntities().values()) {
            var vx = MathHelper.linearInterpolate(entity.xo, entity.x, delta);
            var vy = MathHelper.linearInterpolate(entity.yo, entity.y, delta);
            var vz = MathHelper.linearInterpolate(entity.zo, entity.z, delta);

            ShapeRenderer.renderAABB(builder, entity.getCollisionBoxSize());

            this.viewCamera.push().object(new Vector3d(vx, vy, vz)).set();

            VertexBuilderUploader.uploadPointer(builder);

            this.viewCamera.pop();
        }

        builder.free();

    }

    @Override
    public void config(JsonObject json) {
        this.allowDebug = json.get("allow_debug").getAsBoolean();
    }
}
