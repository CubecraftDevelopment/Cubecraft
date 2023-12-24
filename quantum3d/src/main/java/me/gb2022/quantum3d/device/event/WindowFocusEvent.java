package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the change in focus state of a window.
 * This event is triggered when the focus of a window changes, i.e., when the window
 * gains or loses focus.
 */
public class WindowFocusEvent extends WindowEvent {

    private final boolean focus; // Indicates whether the window gained focus (true) or lost focus (false).

    /**
     * Constructs a new WindowFocusEvent.
     *
     * @param window The window associated with this event.
     * @param focus  Indicates whether the window gained focus (true) or lost focus (false).
     */
    public WindowFocusEvent(Window window, boolean focus) {
        super(window);
        this.focus = focus;
    }

    /**
     * Checks if the window gained focus or lost focus.
     *
     * @return True if the window gained focus, false if it lost focus.
     */
    public boolean isFocus() {
        return focus;
    }
}
