package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;

/**
 * An abstract base class for mouse-related events.
 * Represents events that are triggered by mouse actions.
 */
public abstract class MouseEvent extends WindowEvent {

    private final Mouse mouse; // The mouse instance associated with the event.

    /**
     * Constructs a new MouseEvent.
     *
     * @param window The window associated with this event.
     * @param mouse  The mouse instance.
     */
    public MouseEvent(Window window, Mouse mouse) {
        super(window);
        this.mouse = mouse;
    }

    /**
     * Retrieves the mouse instance associated with the event.
     *
     * @return The mouse instance.
     */
    public Mouse getMouse() {
        return mouse;
    }
}
