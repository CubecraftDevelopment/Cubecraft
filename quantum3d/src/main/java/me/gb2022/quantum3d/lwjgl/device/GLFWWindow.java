package me.gb2022.quantum3d.lwjgl.device;


import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.listener.WindowListener;
import me.gb2022.quantum3d.lwjgl.deprecated.BufferAllocation;
import me.gb2022.quantum3d.lwjgl.deprecated.platform.MonitorInfo;
import me.gb2022.quantum3d.lwjgl.deprecated.textures.ImageUtil;
import org.lwjgl.glfw.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class GLFWWindow extends Window {
    private final GLFWWindowSizeCallback windowSizeCallback;
    private final GLFWWindowPosCallback windowPosCallback;
    private final GLFWWindowFocusCallback windowFocusCallback;
    private final GLFWWindowIconifyCallback windowIconifyCallback;
    private final GLFWWindowCloseCallback windowCloseCallback;
    private final GLFWWindowContentScaleCallback windowContentScaleCallback;
    private final GLFWWindowMaximizeCallback windowMaximizeCallback;
    private final GLFWWindowRefreshCallback windowRefreshCallback;
    private long handle;
    private boolean iconified;
    private boolean focused;
    private int x;
    private int y;
    private int width = 1280;
    private int height = 720;
    private String title = "Quantum3d Window(LWJGL-GLFW Context)";
    private boolean fullScreen;
    private boolean vsync;
    private boolean latestResized;
    private int latestWidth;
    private int latestHeight;
    private int lastNFullScreenWidth;
    private int lastNFullScreenHeight;
    private int lastNFullScreenX;
    private int lastNFullScreenY;
    private boolean resizeable;

    public GLFWWindow() {
        this.windowSizeCallback = GLFWWindowSizeCallback.create((l, w, h) -> {
            this.latestResized = true;
            this.latestWidth = w;
            this.latestHeight = h;
            for (WindowListener listener : this.getListeners()) {
                listener.onSizeEvent(this, w, h);
            }
        });
        this.windowPosCallback = GLFWWindowPosCallback.create((l, i, i1) -> {
            this.x = i;
            this.y = i1;
            for (WindowListener listener : this.getListeners()) {
                listener.onSizeEvent(this, i, i1);
            }
        });
        this.windowFocusCallback = GLFWWindowFocusCallback.create((window, focus) -> {
            this.focused = focus;
            for (WindowListener listener : this.getListeners()) {
                listener.onFocusEvent(this, focus);
            }
        });
        this.windowIconifyCallback = GLFWWindowIconifyCallback.create((window, iconified) -> {
            this.iconified = iconified;
            for (WindowListener listener : this.getListeners()) {
                listener.onIconifyEvent(this, iconified);
            }
        });
        this.windowCloseCallback = GLFWWindowCloseCallback.create((window) -> {
            for (WindowListener listener : this.getListeners()) {
                listener.onCloseEvent(this);
            }
        });
        this.windowContentScaleCallback = GLFWWindowContentScaleCallback.create((window, xScale, yScale) -> {
            for (WindowListener listener : this.getListeners()) {
                listener.onContentScaleEvent(this, xScale, yScale);
            }
        });
        this.windowRefreshCallback = GLFWWindowRefreshCallback.create((window) -> {
            for (WindowListener listener : this.getListeners()) {
                listener.onRefreshEvent(this);
            }
        });
        this.windowMaximizeCallback = GLFWWindowMaximizeCallback.create((window, maximized) -> {
            for (WindowListener listener : this.getListeners()) {
                listener.onMaximizeEvent(this, maximized);
            }
        });
    }

    @Override
    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
        if (latestResized) {
            latestResized = false;
            width = latestWidth;
            height = latestHeight;
        }
    }

    @Override
    public void create() {
        GLFWErrorCallback.createPrint(System.err).set();
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);
        if (this.handle == 0L) {
            throw new IllegalStateException("Failed to create Display window");
        }
        GLFW.glfwDefaultWindowHints();

        MonitorInfo monitorInfo = getMonitorInfo();
        this.x = (monitorInfo.width() - this.width) / 2;
        this.y = (monitorInfo.height() - this.height) / 2;
        GLFW.glfwSetWindowPos(this.handle, this.x, this.y);
        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwShowWindow(this.handle);

        GLFW.glfwSetWindowSizeCallback(this.handle, this.windowSizeCallback);
        GLFW.glfwSetWindowPosCallback(this.handle, this.windowPosCallback);
        GLFW.glfwSetWindowFocusCallback(this.handle, this.windowFocusCallback);
        GLFW.glfwSetWindowIconifyCallback(this.handle, this.windowIconifyCallback);
        GLFW.glfwSetWindowCloseCallback(this.handle, this.windowCloseCallback);
        GLFW.glfwSetWindowContentScaleCallback(this.handle, this.windowContentScaleCallback);
        GLFW.glfwSetWindowRefreshCallback(this.handle, this.windowRefreshCallback);
        GLFW.glfwSetWindowMaximizeCallback(this.handle, this.windowMaximizeCallback);
    }

    @Override
    public void destroy() {
        this.windowSizeCallback.free();
        this.windowPosCallback.free();
        this.windowFocusCallback.free();
        this.windowIconifyCallback.free();
        this.windowMaximizeCallback.free();
        this.windowCloseCallback.free();
        this.windowRefreshCallback.free();
        this.windowContentScaleCallback.free();
        GLFW.glfwDestroyWindow(this.handle);
    }

    public MonitorInfo getMonitorInfo() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(monitor));
        return new MonitorInfo(vidMode.width(), vidMode.height(), vidMode.blueBits() + vidMode.greenBits() + vidMode.redBits(), vidMode.refreshRate());
    }

    public long getHandle() {
        return this.handle;
    }

    @Override
    public boolean isVsync() {
        return this.vsync;
    }

    @Override
    public void setVsync(boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        this.vsync = vsync;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    @Override
    public boolean isFullscreen() {
        return this.fullScreen;
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        if (this.lastNFullScreenWidth == 0) {
            this.lastNFullScreenWidth = this.width;
            this.lastNFullScreenHeight = this.height;
            this.lastNFullScreenX = this.x;
            this.lastNFullScreenY = this.y;
        }

        this.fullScreen = fullscreen;
        if (this.fullScreen) {
            this.lastNFullScreenWidth = this.width;
            this.lastNFullScreenHeight = this.height;
            this.lastNFullScreenX = this.x;
            this.lastNFullScreenY = this.y;
            GLFW.glfwSetWindowMonitor(this.handle, GLFW.glfwGetPrimaryMonitor(), 0, 0, getMonitorInfo().width(), getMonitorInfo().height(), getMonitorInfo().freshRate());
        } else {
            GLFW.glfwSetWindowMonitor(handle, 0, this.lastNFullScreenX, this.lastNFullScreenY, this.lastNFullScreenWidth, this.lastNFullScreenHeight, this.getMonitorInfo().freshRate());
        }
    }

    @Override
    public void setIcon(InputStream in) {
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

    @Override
    public void setSize(int width, int height) {
        GLFW.glfwSetWindowSize(this.handle, width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void setPos(int x, int y) {
        GLFW.glfwSetWindowSize(this.handle, x, y);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public boolean isIconified() {
        return this.iconified;
    }

    @Override
    public boolean isResizeable() {
        return this.resizeable;
    }

    @Override
    public void setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizeable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    @Override
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }


    @Override
    public String toString() {
        return "GLFW_WINDOW@" + this.handle;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}