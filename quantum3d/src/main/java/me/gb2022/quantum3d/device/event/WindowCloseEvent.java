package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the action of closing a window.
 * This event is triggered when a window is requested to be closed.
 */
public class WindowCloseEvent extends WindowEvent {

    /**
     * Constructs a new WindowCloseEvent.
     *
     * @param window The window associated with this event.
     */
    public WindowCloseEvent(Window window) {
        super(window);
    }
}
