package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;

/**
 * An event representing the change in mouse position.
 * This event is triggered when the mouse's position changes.
 */
public final class MousePosEvent extends MouseEvent {

    private final float x;        // The new X-coordinate of the mouse.
    private final float y;        // The new Y-coordinate of the mouse.
    private final float deltaX;   // The change in X-coordinate since the last position.
    private final float deltaY;   // The change in Y-coordinate since the last position.

    /**
     * Constructs a new MousePosEvent.
     *
     * @param window The window associated with this event.
     * @param mouse  The mouse instance.
     * @param x      The new X-coordinate of the mouse.
     * @param y      The new Y-coordinate of the mouse.
     * @param deltaX The change in X-coordinate since the last position.
     * @param deltaY The change in Y-coordinate since the last position.
     */
    public MousePosEvent(Window window, Mouse mouse, float x, float y, float deltaX, float deltaY) {
        super(window, mouse);
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Retrieves the new X-coordinate of the mouse.
     *
     * @return The new X-coordinate of the mouse.
     */
    public float getX() {
        return x;
    }

    /**
     * Retrieves the new Y-coordinate of the mouse.
     *
     * @return The new Y-coordinate of the mouse.
     */
    public float getY() {
        return y;
    }

    /**
     * Retrieves the change in X-coordinate since the last position.
     *
     * @return The change in X-coordinate since the last position.
     */
    public float getDeltaX() {
        return deltaX;
    }

    /**
     * Retrieves the change in Y-coordinate since the last position.
     *
     * @return The change in Y-coordinate since the last position.
     */
    public float getDeltaY() {
        return deltaY;
    }
}
