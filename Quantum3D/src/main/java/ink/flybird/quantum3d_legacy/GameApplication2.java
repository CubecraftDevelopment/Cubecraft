package ink.flybird.quantum3d_legacy;

import ink.flybird.fcommon.threading.LoopTickingThread;
import ink.flybird.quantum3d_legacy.platform.Window;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * A simple client game application class with window and timer binding.
 *
 * @author GrassBlock2022
 */
public abstract class GameApplication2 extends LoopTickingThread {
    private final Window window = new Window();
    private int ticks, frames;
    private int tps, fps;
    private long lastTime;


    //game app
    public abstract void render();

    public abstract void update();

    public abstract void quit();

    public abstract void initialize();

    public abstract void initDisplay(@NotNull Window window);


    //app impl
    @Override
    public final void stop() {
        this.quit();
        this.getWindow().destroy();
        ContextManager.destroyGLFW();
    }

    @Override
    public final void init() {
        this.initDisplay(this.window);
        this.initialize();
    }

    @Override
    public final void shortTick() {
        this.frames++;
        GLUtil.checkError("app:pre");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);//16640
        if (window.isWindowCloseRequested()) {
            this.stop();
        }
        this.render();
        GLUtil.checkError("app:post");
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
    public void onException(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onError(Error error) {
        error.printStackTrace();
    }

    //member access
    public Window getWindow() {
        return this.window;
    }

    public final int getFPS() {
        return this.fps;
    }

    public final int getTPS() {
        return this.tps;
    }
}
