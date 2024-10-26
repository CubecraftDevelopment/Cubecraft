package me.gb2022.quantum3d.device.event;

import me.gb2022.commons.event.CancellableEvent;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.Window;

/**
 * An abstract base class representing a keyboard event.
 * Implements the Event interface.
 */
public abstract class KeyboardEvent extends CancellableEvent {
    private final Window window;
    private final Keyboard keyboard;

    /**
     * Constructs a KeyboardEvent instance.
     *
     * @param window   The Window where the keyboard event occurred.
     * @param keyboard The Keyboard instance associated with the event.
     */
    public KeyboardEvent(Window window, Keyboard keyboard) {
        this.window = window;
        this.keyboard = keyboard;
    }

    /**
     * Retrieves the Window associated with the keyboard event.
     *
     * @return The Window instance where the event occurred.
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Retrieves the Keyboard instance associated with the event.
     *
     * @return The Keyboard instance.
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }
}
