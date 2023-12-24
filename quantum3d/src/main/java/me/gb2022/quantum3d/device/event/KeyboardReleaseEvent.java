package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Window;

/**
 * Represents an event that occurs when a keyboard button is released.
 * Extends the base KeyboardEvent class.
 */
public final class KeyboardReleaseEvent extends KeyboardEvent {

    private final KeyboardButton key;

    /**
     * Constructs a KeyboardReleaseEvent instance.
     *
     * @param window   The Window where the keyboard event occurred.
     * @param keyboard The Keyboard instance associated with the event.
     * @param key      The KeyboardButton that was released.
     */
    public KeyboardReleaseEvent(Window window, Keyboard keyboard, KeyboardButton key) {
        super(window, keyboard);
        this.key = key;
    }

    /**
     * Retrieves the KeyboardButton that was released.
     *
     * @return The KeyboardButton instance representing the released key.
     */
    public KeyboardButton getKey() {
        return key;
    }
}
