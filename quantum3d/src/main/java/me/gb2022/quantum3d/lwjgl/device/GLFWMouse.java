package me.gb2022.quantum3d.lwjgl.device;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.listener.MouseListener;
import me.gb2022.quantum3d.lwjgl.device.GLFWWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public final class GLFWMouse extends Mouse {
    private final GLFWCursorPosCallback cursorPosCallback;
    private final GLFWMouseButtonCallback buttonCallback;
    private final GLFWScrollCallback scrollCallback;
    private long handle;
    private float x, y;

    public GLFWMouse(GLFWWindow window) {
        super(window);
        this.handle = window.getHandle();
        this.cursorPosCallback = GLFWCursorPosCallback.create((_window, xPos, yPos) -> {
            for (MouseListener listener : GLFWMouse.this.getListeners()) {
                listener.onPosEvent(this.getWindow(), this, (float) xPos, (float) yPos, (float) (xPos - x), (float) (yPos - y));
            }
            x = (float) xPos;
            y = (float) yPos;
        });
        this.buttonCallback = GLFWMouseButtonCallback.create((_window, button, action, mods) -> {
            for (MouseListener listener : GLFWMouse.this.getListeners()) {
                if (action == 1) {
                    listener.onPressEvent(this.getWindow(), this, MouseButton.of(button));
                    return;
                }
                listener.onClickEvent(this.getWindow(), this, MouseButton.of(button));
            }
        });
        this.scrollCallback = GLFWScrollCallback.create((_window, xOffset, yOffset) -> {
            for (MouseListener listener : GLFWMouse.this.getListeners()) {
                listener.onScrollEvent(this.getWindow(), this, (float) xOffset, (float) yOffset);
            }
        });
    }

    @Override
    public void create() {
        this.handle = ((GLFWWindow) this.getWindow()).getHandle();
        GLFW.glfwSetCursorPosCallback(this.handle, this.cursorPosCallback);
        GLFW.glfwSetMouseButtonCallback(this.handle, this.buttonCallback);
        GLFW.glfwSetScrollCallback(this.handle, this.scrollCallback);
    }

    @Override
    public void destroy() {
        this.cursorPosCallback.free();
        this.buttonCallback.free();
        this.scrollCallback.free();
    }

    @Override
    public boolean isMouseGrabbed() {
        return GLFW.glfwGetInputMode(this.handle, 208897) == 212995;
    }

    @Override
    public void setMouseGrabbed(boolean grab) {
        GLFW.glfwSetInputMode(this.handle, 208897, grab ? 212995 : 212993);
    }

    @Override
    public void setCursorPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        GLFW.glfwSetCursorPos(this.handle, newX, newY);
    }

    @Override
    public float getCursorX() {
        return this.x;
    }

    @Override
    public float getCursorY() {
        return this.y;
    }


    @Override
    public String toString() {
        return "GLFW_MOUSE@" + this.handle;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
