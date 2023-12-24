package me.gb2022.quantum3d.device.listener;

import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.Window;

/**
 * A listener interface for receiving mouse-related events.
 * Implementing classes can override these methods to respond to mouse events.
 */
public interface MouseListener {

    /**
     * Called when the mouse position changes.
     *
     * @param window The window associated with the event.
     * @param mouse  The mouse instance.
     * @param x      The new X-coordinate of the mouse.
     * @param y      The new Y-coordinate of the mouse.
     * @param deltaX The change in X-coordinate since the last position.
     * @param deltaY The change in Y-coordinate since the last position.
     */
    default void onPosEvent(Window window, Mouse mouse, float x, float y, float deltaX, float deltaY) {
    }

    /**
     * Called when the mouse scroll wheel is scrolled.
     *
     * @param window  The window associated with the event.
     * @param mouse   The mouse instance.
     * @param xOffset The horizontal offset of the scroll.
     * @param yOffset The vertical offset of the scroll.
     */
    default void onScrollEvent(Window window, Mouse mouse, float xOffset, float yOffset) {
    }

    /**
     * Called when a mouse button is pressed.
     *
     * @param window The window associated with the event.
     * @param mouse  The mouse instance.
     * @param button The pressed mouse button.
     */
    default void onPressEvent(Window window, Mouse mouse, MouseButton button) {
    }

    /**
     * Called when a mouse button is clicked (pressed and released quickly).
     *
     * @param window The window associated with the event.
     * @param mouse  The mouse instance.
     * @param button The clicked mouse button.
     */
    default void onClickEvent(Window window, Mouse mouse, MouseButton button) {
    }
}