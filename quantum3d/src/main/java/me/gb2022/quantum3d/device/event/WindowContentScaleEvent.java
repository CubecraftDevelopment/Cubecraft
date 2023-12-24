package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Window;

/**
 * Represents an event that is triggered when the content scale of a window changes.
 * This event provides information about the change in the scaling of the window's content
 * along the X and Y axes.
 */
public class WindowContentScaleEvent extends WindowEvent {

    private final float xScale; // The new content scale along the X axis.
    private final float yScale; // The new content scale along the Y axis.

    /**
     * Constructs a new WindowContentScaleEvent.
     *
     * @param window The window associated with this event.
     * @param xScale The new content scale along the X axis.
     * @param yScale The new content scale along the Y axis.
     */
    public WindowContentScaleEvent(Window window, float xScale, float yScale) {
        super(window);
        this.xScale = xScale;
        this.yScale = yScale;
    }

    /**
     * Gets the new content scale along the X axis.
     *
     * @return The new content scale along the X axis.
     */
    public float getXScale() {
        return xScale;
    }

    /**
     * Gets the new content scale along the Y axis.
     *
     * @return The new content scale along the Y axis.
     */
    public float getYScale() {
        return yScale;
    }
}
