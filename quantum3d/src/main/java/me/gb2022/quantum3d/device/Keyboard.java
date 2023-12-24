package me.gb2022.quantum3d.device;

import me.gb2022.quantum3d.device.listener.KeyboardListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class Keyboard represents a keyboard input device within the Quantum3D platform.
 * It provides methods to manage keyboard event listeners and check the status of specific keys.
 */
public abstract class Keyboard implements Device {
    private final Window window;
    private final List<KeyboardListener> listeners = new ArrayList<>(32);
    private boolean keyDownStatus;
    private int keyDownCount;
    private long lastTime;

    /**
     * Constructs a Keyboard with the associated window.
     *
     * @param window The Window associated with this keyboard.
     */
    protected Keyboard(Window window) {
        this.window = window;
    }

    /**
     * Get the Window associated with this keyboard.
     *
     * @return The associated Window instance.
     */
    public final Window getWindow() {
        return this.window;
    }

    /**
     * Add a KeyboardListener to receive keyboard events from this device.
     *
     * @param listener The KeyboardListener to be added.
     */
    public final void addListener(KeyboardListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a previously added KeyboardListener from receiving events.
     *
     * @param listener The KeyboardListener to be removed.
     */
    public final void removeListener(KeyboardListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get a list of all registered KeyboardListeners for this device.
     *
     * @return A list of registered KeyboardListeners.
     */
    public final List<KeyboardListener> getListeners() {
        return this.listeners;
    }

    /**
     * Check if a specific keyboard key is currently in the pressed state.
     *
     * @param key The KeyboardButton representing the key to check.
     * @return True if the key is currently pressed, otherwise false.
     */
    public abstract boolean isKeyDown(KeyboardButton key);

    /**
     * Check if a specific keyboard key has been double-clicked within a certain time period.
     *
     * @param key         The KeyboardButton representing the key to check.
     * @param timeElapsed The time elapsed since the last key press, in seconds.
     * @return True if the key has been double-clicked within the specified time, otherwise false.
     */
    public final boolean isKeyDoubleClicked(KeyboardButton key, float timeElapsed) {
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
                if (currentTime - lastTime < timeElapsed) {
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
}