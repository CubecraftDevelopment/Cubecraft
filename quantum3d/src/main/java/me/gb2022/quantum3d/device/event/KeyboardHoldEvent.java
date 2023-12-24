package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Window;

/**
 * Represents an event that occurs when a keyboard button is held down.
 * Extends the base KeyboardEvent class.
 */
public final class KeyboardHoldEvent extends KeyboardEvent {

    private final KeyboardButton key;

    /**
     * Constructs a KeyboardHoldEvent instance.
     *
     * @param window   The Window where the keyboard event occurred.
     * @param keyboard The Keyboard instance associated with the event.
     * @param key      The KeyboardButton that is being held down.
     */
    public KeyboardHoldEvent(Window window, Keyboard keyboard, KeyboardButton key) {
        super(window, keyboard);
        this.key = key;
    }

    /**
     * Retrieves the KeyboardButton that is being held down.
     *
     * @return The KeyboardButton instance representing the held key.
     */
    public KeyboardButton getKey() {
        return key;
    }
}
