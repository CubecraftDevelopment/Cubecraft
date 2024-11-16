package net.cubecraft.client.gui;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.util.GLUtil;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.animation.NoneAnimation;
import net.cubecraft.client.gui.animation.ScreenAnimationController;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.registry.ClientSettingRegistry;
import net.cubecraft.world.WorldContext;
import org.lwjgl.opengl.GL11;

public final class GUIContext extends ClientComponent {
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private Window window;
    private ScreenAnimationController animationController = new NoneAnimation();
    private int fixedMouseX, fixedMouseY;
    private Screen previous, screen, next;
    private int timer = 0;


    @Override
    public void deviceSetup(CubecraftClient client, Window window, DeviceContext ctx) {
        this.window = window;
    }

    @Override
    public void clientSetup(CubecraftClient client) {
        this.client.getDeviceEventBus().registerEventListener(this);
    }

    @Override
    public void worldContextChange(WorldContext context) {
        if (context != null) {
            this.setScreen(new HUDScreen());
        }
    }

    @Override
    public void tick() {
        if (this.screen != null) {
            this.screen.tick();
        }
        if (this.next != null) {
            this.next.tick();
        }

        this.animationController.tick(this.timer);

        if (this.animationController.isDone()) {
            this.previous = this.screen;
            this.screen = this.next;
            this.next = null;

            this.setAnimationController(new NoneAnimation());
        }

        this.timer++;
    }

    @Override
    public void render(DisplayScreenInfo info, float delta) {
        GLUtil.checkError("PreGUIRender");
        GL11.glPushMatrix();

        var ww = this.window.getWidth();
        var wh = this.window.getHeight();
        var sw = info.getScreenWidth();
        var sh = info.getScreenHeight();

        GLUtil.setupOrthogonalCamera(0, 0, ww, wh, sw, sh);
        GLUtil.enableDepthTest();
        GLUtil.enableBlend();
        GL11.glDisable(GL11.GL_FOG);

        this.animationController.render(this.screen, this.next, info, delta);

        GL11.glPopMatrix();

        GLUtil.checkError("PostGUIRender");
    }


    //----[switch]----
    public void setAnimationController(ScreenAnimationController animationController) {
        this.animationController = animationController;
        this.timer = 0;
    }

    public void setScreen(Screen screen) {
        this.previous = this.screen;

        if (this.screen != null) {
            this.screen.release();
        }

        this.screen = screen;
        this.screen.init();
    }

    public void setScreen(ScreenBuilder builder, ScreenAnimationController transition) {
        this.setScreen(builder.build(), transition);
    }

    public void setScreen(Screen screen, ScreenAnimationController transition) {
        if (transition == null) {
            this.setScreen(screen);
        }

        this.next = screen;
        this.setAnimationController(transition);
    }


    //----[access]----
    public Window getWindow() {
        return window;
    }

    public int getTimer() {
        return timer;
    }

    public Screen getNext() {
        return next;
    }

    public Screen getPrevious() {
        return previous;
    }

    public Screen getScreen() {
        return screen;
    }

    public SimpleEventBus getEventBus() {
        return eventBus;
    }

    public SimpleEventBus getDeviceEventBus() {
        return this.client.getDeviceEventBus();
    }

    public ScreenAnimationController getAnimationController() {
        return animationController;
    }


    //----[mouse]----
    public int getFixedMouseX() {
        return fixedMouseX;
    }

    public int getFixedMouseY() {
        return fixedMouseY;
    }

    @EventHandler
    public void onMousePos(MousePosEvent e) {
        double scale = ClientSettingRegistry.getFixedGUIScale();
        this.fixedMouseX = (int) (e.getX() / scale);
        this.fixedMouseY = (int) (e.getY() / scale);
    }
}
