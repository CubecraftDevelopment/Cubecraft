package me.gb2022.quantum3d.device;

import me.gb2022.quantum3d.device.listener.MouseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class Mouse represents a mouse input device within the Quantum3D platform.
 * It provides methods to manage mouse event listeners and control mouse-related functionality.
 */
public abstract class Mouse implements Device {

    private final Window window;
    private final List<MouseListener> listeners = new ArrayList<>(32);

    /**
     * Constructs a Mouse with the associated window.
     *
     * @param window The Window associated with this mouse.
     */
    protected Mouse(Window window) {
        this.window = window;
    }

    /**
     * Get the Window associated with this mouse.
     *
     * @return The associated Window instance.
     */
    public final Window getWindow() {
        return this.window;
    }

    /**
     * Add a MouseListener to receive mouse events from this device.
     *
     * @param listener The MouseListener to be added.
     */
    public final void addListener(MouseListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a previously added MouseListener from receiving events.
     *
     * @param listener The MouseListener to be removed.
     */
    public final void removeListener(MouseListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get a list of all registered MouseListeners for this device.
     *
     * @return A list of registered MouseListeners.
     */
    public final List<MouseListener> getListeners() {
        return this.listeners;
    }

    /**
     * Check if the mouse is currently grabbed or captured.
     *
     * @return True if the mouse is grabbed, otherwise false.
     */
    public abstract boolean isMouseGrabbed();

    /**
     * Set the grabbed state of the mouse. When the mouse is grabbed, it's confined to the application window.
     *
     * @param grab True to grab the mouse, false to release it.
     */
    public abstract void setMouseGrabbed(boolean grab);

    /**
     * Set the cursor position on the screen.
     *
     * @param newX The new X-coordinate of the cursor.
     * @param newY The new Y-coordinate of the cursor.
     */
    public abstract void setCursorPosition(float newX, float newY);

    /**
     * Get the X-coordinate of the cursor on the screen.
     *
     * @return The X-coordinate of the cursor.
     */
    public abstract float getCursorX();

    /**
     * Get the Y-coordinate of the cursor on the screen.
     *
     * @return The Y-coordinate of the cursor.
     */
    public abstract float getCursorY();
}
