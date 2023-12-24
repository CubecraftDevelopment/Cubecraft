package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An abstract base class representing events related to a window.
 * This class serves as a foundation for window-related event classes.
 */
public abstract class WindowEvent {

    private final Window window; // The window associated with this event.

    /**
     * Constructs a new WindowEvent.
     *
     * @param window The window associated with this event.
     */
    public WindowEvent(Window window) {
        this.window = window;
    }

    /**
     * Retrieves the window associated with this event.
     *
     * @return The window associated with this event.
     */
    public Window getWindow() {
        return window;
    }
}
