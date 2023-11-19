package net.cubecraft.client.render.world;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.hitting.HitBox;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.chunk.pos.ChunkPos;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.HUD)
public class HUDRenderer extends IWorldRenderer {
    public HUDRenderer(Window window, IWorld world, EntityPlayer player, Camera cam) {
        super(window, world, player, cam);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.TRANSPARENT) {
            return;
        }
        this.camera.setUpGlobalCamera(this.window);

        GL11.glLineWidth(4.0f);
        this.renderSelectionBox();
        if (CubecraftClient.CLIENT.getKeyboard().isKeyDown(KeyboardButton.KEY_F3)) {
            GL11.glLineWidth(1.0f);
            this.renderChunkBorder();
        }
    }

    private void renderSelectionBox() {
        if (this.player.hitResult == null || !(this.player.hitResult.instanceOf(IBlockAccess.class))) {
            return;
        }

        GL11.glPushMatrix();
        HitBox aabb = this.player.hitResult.getHitBox();
        IBlockAccess sel = this.player.hitResult.getObject(IBlockAccess.class);
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(64, DrawMode.LINES);
        builder.begin();
        this.camera.setupObjectCamera(aabb.minPos());

        ShapeRenderer.renderAABB(builder, new AABB(0,0,0,1,1,1));


        builder.end();
        builder.uploadPointer();
        builder.free();
        GL11.glPopMatrix();
    }

    private void renderChunkBorder() {
        GL11.glPushMatrix();
        ChunkPos pos = ChunkPos.fromWorldPos((long) this.player.x, (long) this.player.z);
        this.camera.setupObjectCamera(new Vector3d(pos.getX() * 16, 0, pos.getZ() * 16));
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(256, DrawMode.LINES);
        builder.begin();

        int y = (int) (this.player.y) / 16 * 16;
        builder.color(1, 1, 1, 0.5f);
        ShapeRenderer.renderAABB(builder, new AABB(-0.001, y, -0.001, 16.001, y + 16, 16.001));

        builder.color(1, 1, 1, 0.5f);
        AABB aabb = new AABB(0, 0, 0, 16, 512, 16);
        ShapeRenderer.renderAABB(builder, aabb);

        builder.end();
        builder.uploadPointer();
        builder.free();
        GL11.glPopMatrix();
    }
}
