package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the change in size of a window.
 * This event is triggered when the size of a window changes.
 */
public class WindowSizeEvent extends WindowEvent {

    private final int width;  // The new width of the window.
    private final int height; // The new height of the window.

    /**
     * Constructs a new WindowSizeEvent.
     *
     * @param window The window associated with this event.
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    public WindowSizeEvent(Window window, int width, int height) {
        super(window);
        this.width = width;
        this.height = height;
    }

    /**
     * Retrieves the new width of the window.
     *
     * @return The new width of the window.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the new height of the window.
     *
     * @return The new height of the window.
     */
    public int getHeight() {
        return height;
    }
}
