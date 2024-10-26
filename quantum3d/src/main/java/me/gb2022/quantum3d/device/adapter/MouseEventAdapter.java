package me.gb2022.quantum3d.device.adapter;

import me.gb2022.commons.event.EventBus;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.device.event.MousePressEvent;
import me.gb2022.quantum3d.device.event.MouseScrollEvent;
import me.gb2022.quantum3d.device.listener.MouseListener;


/**
 * An adapter class that listens for mouse events and translates them into corresponding events.
 */
public final class MouseEventAdapter extends EventAdapter implements MouseListener {

    /**
     * Constructs a Mouse EventAdapter instance.
     *
     * @param eventBus The EventBus instance used to publish events.
     */
    public MouseEventAdapter(SimpleEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void onPosEvent(Window window, Mouse mouse, float x, float y, float deltaX, float deltaY) {
        this.getEventBus().callEvent(new MousePosEvent(window, mouse, x, y, deltaX, deltaY));
    }

    @Override
    public void onScrollEvent(Window window, Mouse mouse, float xOffset, float yOffset) {
        this.getEventBus().callEvent(new MouseScrollEvent(window, mouse, xOffset, yOffset));
    }

    @Override
    public void onPressEvent(Window window, Mouse mouse, MouseButton button) {
        this.getEventBus().callEvent(new MousePressEvent(window, mouse, button));
    }

    @Override
    public void onClickEvent(Window window, Mouse mouse, MouseButton button) {
        this.getEventBus().callEvent(new MouseClickEvent(window, mouse, button));
    }
}
