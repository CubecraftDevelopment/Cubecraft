package me.gb2022.quantum3d.lwjgl.device;

import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class GLFWDeviceContext implements DeviceContext {
    @Override
    public Window window() {
        return new GLFWWindow();
    }

    @Override
    public Mouse mouse(Window window) {
        return new GLFWMouse((GLFWWindow) window);
    }

    @Override
    public Keyboard keyboard(Window window) {
        return new GLFWKeyboard((GLFWWindow) window);
    }

    @Override
    public void initContext() {
        GLFW.glfwInit();
        GLFW.glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int i, long l) {
                throw new IllegalStateException(String.valueOf(i));
            }
        });
    }

    @Override
    public void destroyContext() {
        GLFW.glfwTerminate();
    }
}
