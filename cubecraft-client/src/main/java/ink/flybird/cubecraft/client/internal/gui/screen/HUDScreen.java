package ink.flybird.cubecraft.client.internal.gui.screen;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.screen.ScreenBackgroundType;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import ink.flybird.quantum3d.device.event.MouseScrollEvent;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.entity.Entity;
import io.flybird.cubecraft.world.item.Inventory;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.quantum3d.ShapeRenderer;;
import ink.flybird.quantum3d.platform.Keyboard;
import ink.flybird.quantum3d.textures.Texture2D;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.HitResult;
import ink.flybird.fcommon.math.HittableObject;
import org.lwjgl.opengl.GL11;

public final class HUDScreen extends Screen {
    private final Texture2D actionBar = new Texture2D(false, false);
    private final Texture2D pointer = new Texture2D(false, false);

    private int slot;

    private boolean showGUI = true;

    public HUDScreen() {
        super(true, "cubecraft:hud_screen", ScreenBackgroundType.IN_GAME);
        this.actionBar.generateTexture();
        this.actionBar.load(ClientRenderContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiTexture("cubecraft", "containers/actionbar.png")));
        this.pointer.generateTexture();
        this.pointer.load(ClientRenderContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiTexture("cubecraft", "icons/pointer.png")));
    }

    @Override
    public void render(DisplayScreenInfo info, float interpolationTime) {
        super.render(info, interpolationTime);
        GLUtil.enableBlend();
        if (showGUI) {
            this.renderActionBar(info);
            this.pointer.bind();
            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);
            ShapeRenderer.drawRectUV(
                    info.centerX() - 8, info.centerX() + 8,
                    info.centerY() - 8, info.centerY() + 8,
                    0, 0, 1, 0, 1
            );
            GLUtil.disableBlend();
        }
    }

    @EventHandler
    public void onScroll(MouseScrollEvent e) {
        int i = (int) -e.getYOffset();
        if (i > 0) {
            i = 1;
        }
        if (i < 0) {
            i = -1;
        }
        HUDScreen.this.slot += i;
        if (HUDScreen.this.slot > 8) {
            HUDScreen.this.slot = 0;
        }
        if (HUDScreen.this.slot < 0) {
            HUDScreen.this.slot = 8;
        }
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            CubecraftClient.CLIENT.getMouse().setMouseGrabbed(true);
            CubecraftClient.CLIENT.getPlayer().attack();
        }
        if (e.getButton() == MouseButton.MOUSE_BUTTON_RIGHT) {
            CubecraftClient.CLIENT.getPlayer().interact();
        }
        if (e.getButton() == MouseButton.MOUSE_BUTTON_MIDDLE) {
            HitResult<Entity, IWorld> hitResult = CubecraftClient.CLIENT.getPlayer().hitResult;
            if (hitResult != null) {
                HittableObject<Entity, IWorld> obj = CubecraftClient.CLIENT.getPlayer().hitResult.aabb().getObject();
                Inventory inv = CubecraftClient.CLIENT.getPlayer().getInventory();
                inv.selectItem(obj, this.slot);
            }
        }
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent event) {
        if (event.getKey() == KeyboardButton.KEY_ESCAPE) {
            CubecraftClient.CLIENT.getMouse().setMouseGrabbed(false);
            CubecraftClient.CLIENT.getGuiManager().setScreen("cubecraft:pause_screen.xml");
        }
        if (event.getKey() == KeyboardButton.KEY_F1) {
            HUDScreen.this.showGUI = !HUDScreen.this.showGUI;
        }
    }

    @Override
    public void tick() {
        if (CubecraftClient.CLIENT.controller != null) {
            CubecraftClient.CLIENT.controller.tick();
            CubecraftClient.CLIENT.controller.setSelectedSlot(slot);
        }
        super.tick();
    }


    private void renderActionBar(DisplayScreenInfo info) {
        final float scale = 1.263f;
        GL11.glPushMatrix();
        GL11.glTranslated(info.centerX() - 91 * scale, info.scrHeight() - 22 * scale, 0);
        this.actionBar.bind();
        ShapeRenderer.drawRectUV(
                0, 182 * scale, 0, 22 * scale, 0,
                0, 182 / 256f, 0, 22 / 32f
        );
        float slotBase = slot * 20 * scale;
        ShapeRenderer.drawRectUV(slotBase - 1 * scale, slotBase + 23 * scale, -1 * scale, 23 * scale, 0, 232 / 256f, 1, 0, 24 / 32f);
        GL11.glPopMatrix();
    }
}