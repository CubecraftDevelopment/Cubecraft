package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the maximization state change of a window.
 * This event is triggered when a window is maximized or restored from a maximized state.
 */
public class WindowMaximizeEvent extends WindowEvent {

    private final boolean maximize; // Indicates whether the window was maximized (true) or restored (false).

    /**
     * Constructs a new WindowMaximizeEvent.
     *
     * @param window   The window associated with this event.
     * @param maximize Indicates whether the window was maximized (true) or restored (false).
     */
    public WindowMaximizeEvent(Window window, boolean maximize) {
        super(window);
        this.maximize = maximize;
    }

    /**
     * Checks if the window is in a maximized state.
     *
     * @return True if the window is maximized, false if it is restored.
     */
    public boolean isMaximize() {
        return maximize;
    }
}
