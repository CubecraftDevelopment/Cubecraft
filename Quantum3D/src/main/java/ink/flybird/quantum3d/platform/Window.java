package ink.flybird.quantum3d.platform;


import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.quantum3d.BufferAllocation;
import ink.flybird.quantum3d.textures.ImageUtil;
import ink.flybird.fcommon.event.EventBus;
import org.lwjgl.glfw.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

@Deprecated
public class Window {
    //window
    public final EventBus windowEvent = new SimpleEventBus();
    //keyboard
    boolean keyDownStatus = true;
    int keyDownCount = 0;
    long lastTime;
    private long handle;
    private boolean visible;
    private boolean focused;
    private int x, y;
    private int width = 1280, height = 720;
    private boolean latestResized;
    private String title = "Grass3D window";
    private int latestWidth, latestHeight;
    private boolean fullScreen;
    private boolean iconified;
    private int lastNFullScreenWidth, lastNFullScreenHeight, lastNFullScreenX, lastNFullScreenY;
    //mouse
    private boolean mouseGrabbed = false;

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void setMouseGrabbed(boolean grab) {
        GLFW.glfwSetInputMode(this.handle, 208897, grab ? 212995 : 212993);
        this.mouseGrabbed = grab;
    }

    public void setCursorPosition(int newX, int newY) {
        GLFW.glfwSetCursorPos(this.handle, newX, newY);
    }


    //device
    public void create() {
        //get handle
        GLFWErrorCallback.createPrint(System.err).set();
        handle = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
        if (handle == 0L) {
            throw new IllegalStateException("Failed to create Display window");
        }
        GLFW.glfwDefaultWindowHints();

        //set callback
        initCallBacks();

        //set gl context
        MonitorInfo monitorInfo = getMonitorInfo();
        x = (monitorInfo.width() - width) / 2;
        y = (monitorInfo.height() - height) / 2;
        GLFW.glfwSetWindowPos(handle, x, y);
        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(handle);
    }

    public void destroy() {
        //Callbacks.KeyCallback.uninstall(this);
        //Callbacks.CharCallback.uninstall(this);

        //Callbacks.MouseButtonCallback.uninstall(this);
        //Callbacks.MouseScrollCallback.uninstall(this);
        //Callbacks.MouseCursorPositionCallback.uninstall(this);

        GLFW.glfwDestroyWindow(handle);
    }

    private void initCallBacks() {
        GLFW.glfwSetWindowSizeCallback(this.handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long l, int w, int h) {
                latestResized = true;
                latestWidth = w;
                latestHeight = h;
            }
        });
        GLFW.glfwSetWindowFocusCallback(this.handle, new GLFWWindowFocusCallback() {
            public void invoke(long window, boolean focus) {
                focused = focus;
            }
        });
        GLFW.glfwSetWindowIconifyCallback(this.handle, new GLFWWindowIconifyCallback() {
            public void invoke(long window, boolean iconified) {
                visible = iconified;
            }
        });
        GLFW.glfwSetWindowPosCallback(this.handle, new GLFWWindowPosCallback() {
            @Override
            public void invoke(long l, int i, int i1) {
                x = i;
                y = i1;
            }
        });
        GLFW.glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int i, long l) {
                throw new IllegalStateException(String.valueOf(i));
            }
        });
        GLFW.glfwSetWindowIconifyCallback(this.handle, new GLFWWindowIconifyCallback() {
            @Override
            public void invoke(long window, boolean iconified) {
                Window.this.iconified=iconified;
            }
        });
    }

    public EventBus getEventBus() {
        return windowEvent;
    }


    //display
    public MonitorInfo getMonitorInfo() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(monitor));
        return new MonitorInfo(
                vidMode.width(), vidMode.height(),
                vidMode.blueBits() + vidMode.greenBits() + vidMode.redBits(),
                vidMode.refreshRate()
        );
    }

    public void update() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
        if (latestResized) {
            latestResized = false;
            width = latestWidth;
            height = latestHeight;
        }
    }

    public void setWindowVsyncEnable(boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public void setWindowTitle(String title) {
        GLFW.glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    public long getWindowHandle() {
        return handle;
    }

    public boolean isWindowFullscreen() {
        return fullScreen;
    }

    public void setWindowFullscreen(boolean fullscreen) {
        fullScreen = fullscreen;
        if (fullScreen) {
            lastNFullScreenWidth = width;
            lastNFullScreenHeight = height;
            lastNFullScreenX = x;
            lastNFullScreenY = y;
            GLFW.glfwSetWindowMonitor(handle, GLFW.glfwGetPrimaryMonitor(),
                    0, 0,
                    getMonitorInfo().width(), getMonitorInfo().height(), getMonitorInfo().freshRate()
            );

        } else {
            GLFW.glfwSetWindowMonitor(handle, 0,
                    lastNFullScreenX, lastNFullScreenY,
                    lastNFullScreenWidth, lastNFullScreenHeight, 60
            );
        }
    }

    public boolean isWindowCloseRequested() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public boolean isWindowActive() {
        return focused;
    }

    public void setWindowIcon(InputStream in) {
        GLFWImage image = GLFWImage.malloc();
        BufferedImage img;
        try {
            img = ImageIO.read(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteBuffer buffer = ImageUtil.getByteFromBufferedImage_RGBA(img);
        image.set(img.getWidth(), img.getHeight(), buffer);
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
        GLFW.glfwSetWindowIcon(handle, images);
        images.free();
        image.free();
        BufferAllocation.free(buffer);
    }

    public void setWindowSize(int width, int height) {
        GLFW.glfwSetWindowSize(handle, width, height);
    }

    public void setWindowPos(int x, int y) {
        GLFW.glfwSetWindowSize(handle, x, y);
    }

    public int getWindowWidth() {
        return width;
    }

    public int getWindowHeight() {
        return height;
    }

    public boolean isWindowVisible() {
        return visible;
    }

    public float getAspect() {
        return width / (float) height;
    }

    public void hint(int name, int value) {
        GLFW.glfwWindowHint(name, value);
    }


    //keyboard
    public boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(this.handle, Keyboard.toGlfwKey(key)) == 1;
    }

    public boolean isKeyDoubleClicked(int key, float timeElapse) {
        if (this.isKeyDown(key)) {
            if (!keyDownStatus) {
                keyDownStatus = true;
                if (keyDownCount == 0) {// 如果按住数量为 0
                    lastTime = System.currentTimeMillis();// 记录最后时间
                }
                keyDownCount++;
            }
        }
        if (!this.isKeyDown(key)) {
            keyDownStatus = false;
        }
        if (keyDownStatus) {
            if (keyDownCount >= 2) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime < timeElapse) {
                    lastTime = currentTime;
                    keyDownCount = 0;
                    return true;//返回结果，确认双击
                } else {
                    lastTime = System.currentTimeMillis();  // 记录最后时间
                    keyDownCount = 1;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.handle);
    }

    public boolean isIconified() {
        return this.iconified;
    }
}