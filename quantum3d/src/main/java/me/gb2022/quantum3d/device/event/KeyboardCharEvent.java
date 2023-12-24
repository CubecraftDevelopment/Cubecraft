package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Window;

/**
 * Represents an event that occurs when a character is input via the keyboard.
 * Extends the base KeyboardEvent class.
 */
public final class KeyboardCharEvent extends KeyboardEvent {

    private final char character;

    /**
     * Constructs a KeyboardCharEvent instance.
     *
     * @param window    The Window where the keyboard event occurred.
     * @param keyboard  The Keyboard instance associated with the event.
     * @param character The character that was input.
     */
    public KeyboardCharEvent(Window window, Keyboard keyboard, char character) {
        super(window, keyboard);
        this.character = character;
    }

    /**
     * Retrieves the character that was input.
     *
     * @return The character that was input via the keyboard.
     */
    public char getCharacter() {
        return character;
    }
}