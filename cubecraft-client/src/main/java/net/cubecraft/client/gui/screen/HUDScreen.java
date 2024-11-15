package net.cubecraft.client.gui.screen;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.AnyClickInputEvent;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import me.gb2022.quantum3d.texture.Texture2D;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.ShapeRenderer;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.render.item.ItemRenderer;
import net.cubecraft.client.util.IMBlocker;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.blocks.Blocks;
import org.lwjgl.opengl.GL11;

public final class HUDScreen extends Screen {
    private final Texture2D actionBar = new Texture2D(false, false);
    private final Texture2D pointer = new Texture2D(false, false);
    private final VertexBuilder builder = ItemRenderer.ALLOCATOR.allocate(VertexFormat.V3F_C3F_T2F, DrawMode.QUADS, 120);

    private boolean showGUI = true;

    public HUDScreen() {
        super(true, "cubecraft:hud_screen", ScreenBackgroundType.IN_GAME);
        this.actionBar.generateTexture();
        this.actionBar.load(ResourceRegistry.ACTION_BAR);
        this.pointer.generateTexture();
        this.pointer.load(ResourceRegistry.POINTER);
    }

    @Override
    public void render(DisplayScreenInfo info, float deltaTime) {
        super.render(info, deltaTime);
        GLUtil.enableBlend();
        if (!this.showGUI) {
            return;
        }


        this.renderActionBar(info);

        var cx = info.getCenterX();
        var cy = info.getCenterY();

        this.builder.reset();
        ShapeRenderer.drawRectUV(this.builder, cx - 14, cx + 14, cy - 14, cy + 14, 99, 0, 1, 0, 1);
        this.pointer.bind();
        VertexBuilderUploader.uploadPointer(this.builder);
    }


    @Override
    public void tick() {
        PlayerController controller = ClientSharedContext.getClient().getPlayerController();
        if (controller != null) {
            controller.tick();
        }
        super.tick();
    }


    private void renderActionBar(DisplayScreenInfo info) {
        final var scale = 1.5f;

        var bx = info.getCenterX() - 91 * scale;
        var by = info.getScreenHeight() - 22 * scale;
        var e = this.getClient().getPlayerController().getEntity();

        if (e == null) {
            return;
        }

        GLUtil.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslated(bx, by, 0);

        GL11.glScalef(scale, scale, 1);

        for (var i = 0; i < 9; i++) {
            var item = e.getInventory().get(i);

            if (item == null) {
                continue;
            }

            var sx = i * 20 + 2;

            var batch = ItemRenderer.getModel(item);


            if (batch != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef(sx + 2.7f, 15.5f, 0.0F);
                GL11.glScalef(9, 9, 9);

                batch.call();

                GL11.glPopMatrix();
            }
        }

        var slotBase = e.getInventory().getActiveSlotId() * 20;

        this.actionBar.bind();
        this.builder.reset();

        ShapeRenderer.drawRectUV(this.builder, 0, 182, 0, 22, -20, 0, 1, 0, 22 / 46f);
        ShapeRenderer.drawRectUV(this.builder, slotBase - 1, slotBase + 23, -1, 22, -10, 0, 24 / 182f, 22 / 46f, 1);

        VertexBuilderUploader.uploadPointer(this.builder);

        GL11.glPopMatrix();
    }

    @Override
    public void getDebug() {
        super.getDebug();

        var client = this.getClient();
        var world = this.getClient().getWorldContext().getWorld();
        var player = this.getClient().getWorldContext().getPlayer();

        var c_pc = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "PC", int.class);
        var c_cr = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "C/R", int.class);
        var c_cd = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "C/D", int.class);
        var c_das = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/A_S", int.class);
        var c_dts = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/T_S", int.class);
        var c_da = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/A", int.class);
        var c_dt = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/T", int.class);
        var c_sc = ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "SC", String.class);

        var e_es = ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "E/S", int.class);
        var e_ea = ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "E/A", int.class);
        var e_bs = ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "BE/S", int.class);
        var e_ba = ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "BE/A", int.class);

        var p_s = ClientSharedContext.QUERY_HANDLER.query("cubecraft:particle_renderer", "success_size", int.class);
        var p_a = ClientSharedContext.QUERY_HANDLER.query("cubecraft:particle_renderer", "all_size", int.class);

        var v_x = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.x);
        var v_y = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.y);
        var v_z = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.z);
        var f_x = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.xRot);
        var f_y = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.yRot);
        var f_z = SharedObjects.SHORT_DECIMAL_FORMAT.format(player.zRot);

        var w_id = world.getId();
        var w_cs = world.getChunkCache().map().size();

        this.debugLeft("C", "C: pc=%d req=%d res=%d, a=%s/%s t=%s/%s cache=%s", c_pc, c_cr, c_cd, c_das, c_da, c_dts, c_dt, c_sc);
        this.debugLeft("E", "E: %d+%d/%d+%d", e_es, e_bs, e_ea, e_ba);
        this.debugLeft("P", "P: %d/%d", p_s, p_a);
        this.debugLeft("_0", "");
        this.debugLeft("W", "W: %s (c=%s)".formatted(w_id, w_cs) + (world.isClient() ? "[client]" : ""));
        this.debugLeft("loc", "Pos: %s/%s/%s", v_x, v_y, v_z);
        this.debugLeft("fac", "Rot: %s/%s/%s", f_x, f_y, f_z);

        this.debugRight("_split_0", "");
        var hit = player.hitResult;
        if (hit != null) {
            if (hit.instanceOf(BlockAccess.class)) {
                var b = hit.getObject(BlockAccess.class);

                this.debugRight("tgt_b", "TargetBlock: %s,%s,%s".formatted(b.getX(), b.getY(), b.getZ()));
                var str = String.valueOf(Blocks.REGISTRY.name(b.getBlockId()));

                if (str == null) {
                    str = "[unknown]";
                }

                this.debugRight("tgt_b_id", str);
            }
        }
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent event) {
        if (event.getKey() == KeyboardButton.KEY_ESCAPE) {
            this.client.getClientDeviceContext().getMouse().setMouseGrabbed(false);
            this.client.getClientGUIContext().setScreen(ScreenBuilder.xml(ResourceRegistry.PAUSE_SCREEN));
        }
        if (event.getKey() == KeyboardButton.KEY_F1) {
            this.showGUI = !this.showGUI;
        }
    }

    @EventHandler
    public void onInput(AnyClickInputEvent event) {
        this.client.getClientDeviceContext().getMouse().setMouseGrabbed(true);
        var controller = ClientSharedContext.getClient().getPlayerController();
        if (controller != null) {
            controller.onInput(event);
        }

        IMBlocker.set(true);
    }
}