package me.gb2022.quantum3d.device.adapter;

import ink.flybird.fcommon.event.EventBus;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.listener.WindowListener;
import me.gb2022.quantum3d.device.event.*;

/**
 * An adapter class that listens for mouse events and translates them into corresponding events.
 */
public final class WindowEventAdapter extends EventAdapter implements WindowListener {

    /**
     * Constructs a Window EventAdapter instance.
     *
     * @param eventBus The EventBus instance used to publish events.
     */
    public WindowEventAdapter(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void onSizeEvent(Window window, int width, int height) {
        this.getEventBus().callEvent(new WindowSizeEvent(window, width, height));
    }

    @Override
    public void onFocusEvent(Window window, boolean focus) {
        this.getEventBus().callEvent(new WindowFocusEvent(window, focus));
    }

    @Override
    public void onPosEvent(Window window, int x, int y) {
        this.getEventBus().callEvent(new WindowPositionEvent(window, x, y));
    }

    @Override
    public void onIconifyEvent(Window window, boolean iconified) {
        this.getEventBus().callEvent(new WindowIconifyEvent(window, iconified));
    }

    @Override
    public void onCloseEvent(Window window) {
        this.getEventBus().callEvent(new WindowCloseEvent(window));
    }

    @Override
    public void onContentScaleEvent(Window window, float xScale, float yScale) {
        this.getEventBus().callEvent(new WindowContentScaleEvent(window, xScale, yScale));
    }

    @Override
    public void onRefreshEvent(Window window) {
        this.getEventBus().callEvent(new WindowRefreshEvent(window));
    }

    @Override
    public void onMaximizeEvent(Window window, boolean maximized) {
        this.getEventBus().callEvent(new WindowMaximizeEvent(window, maximized));
    }
}
