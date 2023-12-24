package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * An event representing a request to refresh the contents of a window.
 * This event is triggered when the contents of a window need to be redrawn or refreshed.
 */
public class WindowRefreshEvent extends WindowEvent {

    /**
     * Constructs a new WindowRefreshEvent.
     *
     * @param window The window associated with this event.
     */
    public WindowRefreshEvent(Window window) {
        super(window);
    }
}
