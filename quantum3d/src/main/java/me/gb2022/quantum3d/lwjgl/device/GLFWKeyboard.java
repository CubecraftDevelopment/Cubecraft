package me.gb2022.quantum3d.lwjgl.device;

import me.gb2022.commons.context.LifetimeCounter;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.listener.KeyboardListener;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

public final class GLFWKeyboard extends Keyboard {
    private final LifetimeCounter counter = new LifetimeCounter();
    private final GLFWKeyCallback keyCallback;
    private final GLFWCharCallback charCallback;
    private long handle;

    public GLFWKeyboard(GLFWWindow window) {
        super(window);
        this.handle = window.getHandle();
        this.keyCallback = GLFWKeyCallback.create((_window, key, scancode, action, mods) -> {
            for (KeyboardListener listener : this.getListeners()) {
                switch (action) {
                    case 0 -> listener.onKeyReleaseEvent(this.getWindow(), this, KeyboardButton.of(key));
                    case 1 -> listener.onKeyPressEvent(this.getWindow(), this, KeyboardButton.of(key));
                    case 2 -> listener.onKeyHoldEvent(this.getWindow(), this, KeyboardButton.of(key));
                }
            }
        });
        this.charCallback = GLFWCharCallback.create((_window, codepoint) -> {
            for (KeyboardListener listener : this.getListeners()) {
                listener.onCharEvent(this.getWindow(), this, (char) codepoint);
            }
        });
    }

    @Override
    public void create() {
        this.counter.allocate();
        this.handle = ((GLFWWindow) this.getWindow()).getHandle();
        GLFW.glfwSetKeyCallback(this.handle, this.keyCallback);
        GLFW.glfwSetCharCallback(this.handle, this.charCallback);
    }

    @Override
    public void destroy() {
        this.counter.release();
        this.charCallback.free();
        this.keyCallback.free();
    }

    @Override
    public boolean isKeyDown(KeyboardButton key) {
        if (!this.counter.isAllocated()) {
            return false;
        }
        return GLFW.glfwGetKey(this.handle, key.getCode()) == 1;
    }

    @Override
    public String toString() {
        return "GLFW_KEYBOARD@" + this.handle;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}