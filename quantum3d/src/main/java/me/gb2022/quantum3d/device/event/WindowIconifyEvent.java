package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the iconification (minimization) state change of a window.
 * This event is triggered when a window is minimized (iconified) or restored from a minimized state.
 */
public class WindowIconifyEvent extends WindowEvent {

    private final boolean iconified; // Indicates whether the window was iconified (true) or restored (false).

    /**
     * Constructs a new WindowIconifyEvent.
     *
     * @param window    The window associated with this event.
     * @param iconified Indicates whether the window was iconified (true) or restored (false).
     */
    public WindowIconifyEvent(Window window, boolean iconified) {
        super(window);
        this.iconified = iconified;
    }

    /**
     * Checks if the window is in an iconified (minimized) state.
     *
     * @return True if the window is iconified (minimized), false if it is restored.
     */
    public boolean isIconified() {
        return iconified;
    }
}