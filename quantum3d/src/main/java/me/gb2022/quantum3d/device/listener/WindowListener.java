package me.gb2022.quantum3d.device.listener;

import me.gb2022.quantum3d.device.Window;

/**
 * Interface for receiving window-related events.
 */
public interface WindowListener {

    /**
     * Called when the window size changes.
     *
     * @param window The window that triggered the event.
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    default void onSizeEvent(Window window, int width, int height) {
    }

    /**
     * Called when the window gains or loses focus.
     *
     * @param window The window that triggered the event.
     * @param focus  `true` if the window gained focus, `false` if it lost focus.
     */
    default void onFocusEvent(Window window, boolean focus) {
    }

    /**
     * Called when the window position changes.
     *
     * @param window The window that triggered the event.
     * @param x      The new X-coordinate position of the window.
     * @param y      The new Y-coordinate position of the window.
     */
    default void onPosEvent(Window window, int x, int y) {
    }

    /**
     * Called when the window is minimized or restored from being minimized.
     *
     * @param window    The window that triggered the event.
     * @param iconified `true` if the window was iconified (minimized), `false` if it was restored.
     */
    default void onIconifyEvent(Window window, boolean iconified) {
    }

    /**
     * Called when the window is being closed.
     *
     * @param window The window that triggered the event.
     */
    default void onCloseEvent(Window window) {
    }

    /**
     * Called when the content scale (DPI) of the window changes.
     *
     * @param window The window that triggered the event.
     * @param xScale The new X-axis content scale.
     * @param yScale The new Y-axis content scale.
     */
    default void onContentScaleEvent(Window window, float xScale, float yScale) {
    }

    /**
     * Called when the window needs to be refreshed.
     *
     * @param window The window that triggered the event.
     */
    default void onRefreshEvent(Window window) {
    }

    /**
     * Called when the window is maximized or restored from being maximized.
     *
     * @param window    The window that triggered the event.
     * @param maximized `true` if the window was maximized, `false` if it was restored.
     */
    default void onMaximizeEvent(Window window, boolean maximized) {
    }
}