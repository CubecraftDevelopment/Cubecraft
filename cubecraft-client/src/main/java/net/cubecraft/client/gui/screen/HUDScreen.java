package net.cubecraft.client.gui.screen;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.MousePressEvent;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.registry.ResourceRegistry;
import org.lwjgl.opengl.GL11;

public final class HUDScreen extends Screen {
    private final Texture2D actionBar = new Texture2D(false, false);
    private final Texture2D pointer = new Texture2D(false, false);

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
        if (showGUI) {
            this.renderActionBar(info);
            this.pointer.bind();
            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);
            ShapeRenderer.drawRectUV(
                    info.getCenterX() - 8, info.getCenterX() + 8,
                    info.getCenterY() - 8, info.getCenterY() + 8,
                    0, 0, 1, 0, 1
            );
            GLUtil.disableBlend();
        }
    }


    @Override
    public void tick() {
        PlayerController controller = CubecraftClient.CLIENT.getPlayerController();
        if (controller != null) {
            controller.tick();
        }
        super.tick();
    }


    private void renderActionBar(DisplayScreenInfo info) {
        final float scale = 1.263f;
        GL11.glPushMatrix();
        GL11.glTranslated(info.getCenterX() - 91 * scale, info.getScreenHeight() - 22 * scale, 0);
        this.actionBar.bind();
        ShapeRenderer.drawRectUV(
                0, 182 * scale, 0, 22 * scale, 0,
                0, 182 / 256f, 0, 22 / 32f
        );
        float slotBase = this.getClient().getPlayer().getInventory().getActiveSlotId() * 20 * scale;
        ShapeRenderer.drawRectUV(slotBase - 1 * scale, slotBase + 23 * scale, -1 * scale, 23 * scale, 0, 232 / 256f, 1, 0, 24 / 32f);
        GL11.glPopMatrix();
    }

    @Override
    public void getDebug() {
        super.getDebug();
        this.debugInfoLeft.put("chunk_renderer", "C: pc=%d req=%d res=%d, a=%s/%s t=%s/%s cache=%s".formatted(
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "pos_cache_size", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "compile_request_size", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "compile_result_size", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "draw_success_size_alpha", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "draw_size_alpha", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "draw_success_size_transparent", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "draw_size_transparent", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "status_cache", String.class)

        ));
        this.debugInfoLeft.put("entity_renderer", "E: %d/%d".formatted(
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "success_size", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:entity_renderer", "all_size", int.class)
        ));

        this.debugInfoLeft.put("particle_renderer", "P: %d/%d".formatted(
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:particle_renderer", "success_size", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:particle_renderer", "all_size", int.class)
        ));
        this.debugInfoLeft.put("_split_0", "");
        this.debugInfoLeft.put("loc", "Cam: %s/%s/%s (%s/%s/%s)".formatted(
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getPlayer().x),
                SharedObjects.LONG_DECIMAL_FORMAT.format(this.client.getPlayer().y),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getPlayer().z),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getPlayer().xRot),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getPlayer().yRot),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getPlayer().zRot)
        ));

        this.debugInfoLeft.put("_split_1", "");
        this.debugInfoLeft.put("world", "World: %s (c=%s)".formatted(
                this.getClient().getClientWorld().getId(),
                this.getClient().getClientWorld().chunks.size()
        ));
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent event) {
        if (event.getKey() == KeyboardButton.KEY_ESCAPE) {
            CubecraftClient.CLIENT.getMouse().setMouseGrabbed(false);
            CubecraftClient.CLIENT.getGuiManager().setScreen(ResourceRegistry.PAUSE_SCREEN);
        }
        if (event.getKey() == KeyboardButton.KEY_F1) {
            this.showGUI = !this.showGUI;
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        CubecraftClient.CLIENT.getMouse().setMouseGrabbed(true);
    }
}