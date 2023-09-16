package ink.flybird.cubecraft.client.internal.renderer.world;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.HitBox;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.block.IBlockAccess;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d.device.Window;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

@TypeItem(WorldRendererType.HUD)
public class HUDRenderer extends IWorldRenderer {
    public HUDRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.TRANSPARENT) {
            return;
        }
        this.parent.setRenderState(this.setting);
        this.camera.setUpGlobalCamera(this.window);

        GL11.glLineWidth(4.0f);
        this.renderSelectionBox();
        if (CubecraftClient.CLIENT.getKeyboard().isKeyDown(KeyboardButton.KEY_F3)) {
            GL11.glLineWidth(1.0f);
            this.renderChunkBorder();
        }

        this.parent.closeState(this.setting);
    }

    private void renderSelectionBox() {
        if (this.player.hitResult == null || !(this.player.hitResult.aabb().getObject() instanceof IBlockAccess sel)) {
            return;
        }

        GL11.glPushMatrix();
        HitBox<Entity, IWorld> aabb = this.player.hitResult.aabb();
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(64, DrawMode.LINES);
        builder.begin();
        this.camera.setupObjectCamera(aabb.minPos());
        for (HitBox<Entity, IWorld> hitBox : sel.getBlock().getSelectionBox(0, 0, 0, sel)) {
            ShapeRenderer.renderAABB(builder, hitBox);
        }

        builder.end();
        builder.uploadPointer();
        builder.free();
        GL11.glPopMatrix();
    }

    private void renderChunkBorder() {
        GL11.glPushMatrix();
        ChunkPos pos = ChunkPos.fromWorldPos((long) this.player.x, (long) this.player.z);
        this.camera.setupObjectCamera(new Vector3d(pos.x() * 16, 0, pos.z() * 16));
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(256, DrawMode.LINES);
        builder.begin();

        GLUtil.enableBlend();

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
