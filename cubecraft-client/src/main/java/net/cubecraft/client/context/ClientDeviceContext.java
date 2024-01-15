package net.cubecraft.client.context;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.adapter.KeyboardEventAdapter;
import me.gb2022.quantum3d.device.adapter.MouseEventAdapter;
import me.gb2022.quantum3d.device.adapter.WindowEventAdapter;

public final class ClientDeviceContext {
    private final EventBus eventBus = new SimpleEventBus();
    private DeviceContext deviceContext;
    private Window window;
    private Mouse mouse;
    private Keyboard keyboard;

    public void init(Window window, DeviceContext deviceContext) {
        this.deviceContext = deviceContext;
        this.window = window;
        this.keyboard = this.getDeviceContext().keyboard(window);
        this.mouse = this.getDeviceContext().mouse(window);
        this.keyboard.create();
        this.mouse.create();
        this.window.addListener(new WindowEventAdapter(this.eventBus));
        this.keyboard.addListener(new KeyboardEventAdapter(this.eventBus));
        this.mouse.addListener(new MouseEventAdapter(this.eventBus));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Window getWindow() {
        return window;
    }

    public DeviceContext getDeviceContext() {
        return deviceContext;
    }

    public void attachListener(Object listener) {
        this.eventBus.registerEventListener(listener);
    }

    public void detachListener(Object listener) {
        this.eventBus.unregisterEventListener(listener);
    }

    public void attachListener(Class<?> listener) {
        this.eventBus.registerEventListener(listener);
    }

    public void detachListener(Class<?> listener) {
        this.eventBus.unregisterEventListener(listener);
    }

    public void destroy() {
        this.mouse.destroy();
        this.keyboard.destroy();
    }
}
