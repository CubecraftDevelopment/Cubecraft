package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the pressing of a mouse button.
 * This event is triggered when a mouse button is pressed.
 */
public final class MousePressEvent extends MouseEvent {

    private final MouseButton button; // The pressed mouse button.

    /**
     * Constructs a new MousePressEvent.
     *
     * @param window The window associated with this event.
     * @param mouse  The mouse instance.
     * @param button The pressed mouse button.
     */
    public MousePressEvent(Window window, Mouse mouse, MouseButton button) {
        super(window, mouse);
        this.button = button;
    }

    /**
     * Retrieves the pressed mouse button.
     *
     * @return The pressed mouse button.
     */
    public MouseButton getButton() {
        return button;
    }
}