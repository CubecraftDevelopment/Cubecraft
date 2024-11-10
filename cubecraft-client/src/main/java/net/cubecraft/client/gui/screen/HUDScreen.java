package net.cubecraft.client.gui.screen;

import me.gb2022.commons.event.EventHandler;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePressEvent;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.util.IMBlocker;
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

            GLUtil.enableBlend();
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);
            ShapeRenderer.drawRectUV(
                    info.getCenterX() - 14, info.getCenterX() + 14,
                    info.getCenterY() - 14, info.getCenterY() + 14,
                    0, 0, 1, 0, 1
            );
            GLUtil.disableBlend();
        }
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
        final float scale = 1.263f;
        GL11.glPushMatrix();
        GL11.glTranslated(info.getCenterX() - 91 * scale, info.getScreenHeight() - 22 * scale, 0);
        this.actionBar.bind();
        ShapeRenderer.drawRectUV(
                0, 182 * scale, 0, 22 * scale, 0,
                0, 182 / 256f, 0, 22 / 32f
        );
        CubecraftClient client1 = this.getClient();
        float slotBase = client1.getClientWorldContext().getPlayer().getInventory().getActiveSlotId() * 20 * scale;
        ShapeRenderer.drawRectUV(slotBase - 1 * scale, slotBase + 23 * scale, -1 * scale, 23 * scale, 0, 232 / 256f, 1, 0, 24 / 32f);
        GL11.glPopMatrix();
    }

    @Override
    public void getDebug() {
        super.getDebug();
        this.debugInfoLeft.put("chunk_renderer", "C: pc=%d req=%d res=%d, a=%s/%s t=%s/%s cache=%s".formatted(
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "PC", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "C/R", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "C/D", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/A_S", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/A", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/T_S", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "D/T", int.class),
                ClientSharedContext.QUERY_HANDLER.query("cubecraft:chunk_renderer", "SC", String.class)
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
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().x),
                SharedObjects.LONG_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().y),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().z),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().xRot),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().yRot),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(this.client.getClientWorldContext().getPlayer().zRot)
        ));

        this.debugInfoLeft.put("_split_1", "");
        CubecraftClient client1 = this.getClient();
        CubecraftClient client2 = this.getClient();
        this.debugInfoLeft.put("world", "World: %s (c=%s)".formatted(
                client2.getClientWorldContext().getWorld().getId(),
                client1.getClientWorldContext().getWorld().getChunkCache().map().size()
        ));
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
    public void onMousePress(MouseClickEvent event) {
        this.client.getClientDeviceContext().getMouse().setMouseGrabbed(true);
        PlayerController controller = ClientSharedContext.getClient().getPlayerController();
        if (controller != null) {
            controller.onClicked(event);
        }

        IMBlocker.set(true);
    }


}