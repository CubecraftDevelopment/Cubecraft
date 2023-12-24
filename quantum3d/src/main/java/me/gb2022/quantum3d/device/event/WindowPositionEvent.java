package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the change in position of a window.
 * This event is triggered when the position of a window changes.
 */
public class WindowPositionEvent extends WindowEvent {

    private final int x; // The new X-coordinate position of the window.
    private final int y; // The new Y-coordinate position of the window.

    /**
     * Constructs a new WindowPositionEvent.
     *
     * @param window The window associated with this event.
     * @param x      The new X-coordinate position of the window.
     * @param y      The new Y-coordinate position of the window.
     */
    public WindowPositionEvent(Window window, int x, int y) {
        super(window);
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the new X-coordinate position of the window.
     *
     * @return The new X-coordinate position of the window.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the new Y-coordinate position of the window.
     *
     * @return The new Y-coordinate position of the window.
     */
    public int getY() {
        return y;
    }
}
