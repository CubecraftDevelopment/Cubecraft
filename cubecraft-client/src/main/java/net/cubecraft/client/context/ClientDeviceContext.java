package net.cubecraft.client.context;

import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.adapter.KeyboardEventAdapter;
import me.gb2022.quantum3d.device.adapter.MouseEventAdapter;
import me.gb2022.quantum3d.device.adapter.WindowEventAdapter;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;

public final class ClientDeviceContext extends ClientComponent {
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private DeviceContext deviceContext;
    private Window window;
    private Mouse mouse;
    private Keyboard keyboard;

    @Override
    public void deviceSetup(CubecraftClient client, Window window, DeviceContext ctx) {
        this.deviceContext = ctx;
        this.window = window;
        this.keyboard = this.getDeviceContext().keyboard(this.window);
        this.mouse = this.getDeviceContext().mouse(this.window);
        this.keyboard.create();
        this.mouse.create();
        this.window.addListener(new WindowEventAdapter(this.eventBus));
        this.keyboard.addListener(new KeyboardEventAdapter(this.eventBus));
        this.mouse.addListener(new MouseEventAdapter(this.eventBus));
    }

    @Override
    public void clientQuit(CubecraftClient client) {
        this.mouse.destroy();
        this.keyboard.destroy();
    }

    public SimpleEventBus getEventBus() {
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
}
