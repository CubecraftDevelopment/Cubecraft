package me.gb2022.quantum3d;

import ink.flybird.fcommon.threading.LoopTickingThread;
import ink.flybird.fcommon.timer.Timer;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.render.RenderContext;

/**
 * A simple client game application class with window and timer binding.
 *
 * @author GrassBlock2022
 */
public abstract class GameApplication extends LoopTickingThread {
    private final DeviceContext deviceContext;
    private final Window window;
    private final RenderContext renderContext;
    private int ticks, frames;
    private int tps, fps;
    private long lastTime;

    public GameApplication(
            DeviceContext deviceContext,
            RenderContext renderContext,
            Timer timer
    ) {
        this.deviceContext = deviceContext;
        this.renderContext = renderContext;
        this.window = deviceContext.window();
        this.timer = timer;
    }

    public abstract void render();

    public abstract void update();

    public abstract void quit();

    public abstract void initialize();

    public abstract void initDevice(Window window);

    @Override
    public final void stop() {
        if (!this.isRunning()) {
            return;
        }
        this.setRunning(false);
        this.quit();
        this.getWindow().destroy();
        this.deviceContext.destroyContext();
    }

    @Override
    public final void init() {
        this.deviceContext.initContext();
        this.getWindow().create();
        this.initDevice(this.window);
        this.renderContext.create();
        this.initialize();
    }

    @Override
    public final void shortTick() {
        this.frames++;
        this.renderContext.checkError("app:pre");
        this.renderContext.clearBuffer();
        if (this.window.isCloseRequested()) {
            this.stop();
            return;
        }
        this.render();
        this.renderContext.checkError("app:post");
        this.window.update();

        if (System.currentTimeMillis() >= this.lastTime + 1000L) {
            this.lastTime = System.currentTimeMillis();
            this.fps = this.frames;
            this.tps = this.ticks;
            this.frames = 0;
            this.ticks = 0;
        }
    }

    @Override
    public final void tick() {
        this.update();
        this.ticks++;
    }

    @Override
    public boolean onException(Exception exception) {
        return true;
    }

    @Override
    public boolean onError(Error error) {
        return true;
    }

    //member access
    public Window getWindow() {
        return this.window;
    }

    public DeviceContext getDeviceContext() {
        return deviceContext;
    }

    public final int getFPS() {
        return this.fps;
    }

    public final int getTPS() {
        return this.tps;
    }

    public RenderContext getRenderContext() {
        return this.renderContext;
    }
}
