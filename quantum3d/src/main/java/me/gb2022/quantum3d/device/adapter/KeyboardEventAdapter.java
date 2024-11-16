package me.gb2022.quantum3d.device.adapter;

import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.event.*;
import me.gb2022.quantum3d.device.listener.KeyboardListener;

/**
 * An adapter class that listens for keyboard events and translates them into corresponding events.
 */
public final class KeyboardEventAdapter extends EventAdapter implements KeyboardListener {

    /**
     * Constructs a Keyboard EventAdapter instance.
     *
     * @param eventBus The EventBus instance used to publish events.
     */
    public KeyboardEventAdapter(SimpleEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void onKeyPressEvent(Window window, Keyboard keyboard, KeyboardButton key) {
        this.getEventBus().callEvent(new KeyboardPressEvent(window, keyboard, key));
        this.getEventBus().callEvent(new AnyClickInputEvent(window, null, keyboard, key, null));
    }

    @Override
    public void onKeyReleaseEvent(Window window, Keyboard keyboard, KeyboardButton key) {
        this.getEventBus().callEvent(new KeyboardReleaseEvent(window, keyboard, key));
    }

    @Override
    public void onKeyHoldEvent(Window window, Keyboard keyboard, KeyboardButton key) {
        this.getEventBus().callEvent(new KeyboardHoldEvent(window, keyboard, key));
    }

    @Override
    public void onCharEvent(Window window, Keyboard keyboard, char codepoint) {
        this.getEventBus().callEvent(new KeyboardCharEvent(window, keyboard, codepoint));
    }
}
