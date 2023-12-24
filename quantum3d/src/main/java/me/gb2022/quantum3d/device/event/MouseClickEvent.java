package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;

/**
 * Represents an event triggered when a mouse button is clicked.
 */
public final class MouseClickEvent extends MouseEvent {

    private final MouseButton button;

    /**
     * Constructs a MouseClickEvent instance.
     *
     * @param window The window where the mouse event occurred.
     * @param mouse  The Mouse instance associated with the event.
     * @param button The MouseButton that was clicked.
     */
    public MouseClickEvent(Window window, Mouse mouse, MouseButton button) {
        super(window, mouse);
        this.button = button;
    }

    /**
     * Retrieves the MouseButton that was clicked.
     *
     * @return The MouseButton instance representing the clicked button.
     */
    public MouseButton getButton() {
        return button;
    }
}
