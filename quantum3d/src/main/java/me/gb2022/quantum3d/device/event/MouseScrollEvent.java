package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the scrolling of the mouse's scroll wheel.
 * This event is triggered when the mouse scroll wheel is scrolled.
 */
public final class MouseScrollEvent extends MouseEvent {

    private final float xOffset; // The horizontal offset of the scroll.
    private final float yOffset; // The vertical offset of the scroll.

    /**
     * Constructs a new MouseScrollEvent.
     *
     * @param window  The window associated with this event.
     * @param mouse   The mouse instance.
     * @param xOffset The horizontal offset of the scroll.
     * @param yOffset The vertical offset of the scroll.
     */
    public MouseScrollEvent(Window window, Mouse mouse, float xOffset, float yOffset) {
        super(window, mouse);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * Retrieves the horizontal offset of the scroll.
     *
     * @return The horizontal offset of the scroll.
     */
    public float getXOffset() {
        return xOffset;
    }

    /**
     * Retrieves the vertical offset of the scroll.
     *
     * @return The vertical offset of the scroll.
     */
    public float getYOffset() {
        return yOffset;
    }
}