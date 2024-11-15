package me.gb2022.quantum3d.device.event;

import me.gb2022.quantum3d.device.*;

public final class AnyClickInputEvent {
    private final Window window;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final KeyboardButton keyboardButton;
    private final MouseButton mouseButton;

    public AnyClickInputEvent(Window window, Mouse mouse, Keyboard keyboard, KeyboardButton keyboardButton, MouseButton mouseButton) {
        this.window = window;
        this.mouse = mouse;
        this.keyboard = keyboard;
        this.keyboardButton = keyboardButton;
        this.mouseButton = mouseButton;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public KeyboardButton getKeyboardButton() {
        return keyboardButton;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public MouseButton getMouseButton() {
        return mouseButton;
    }

    public Window getWindow() {
        return window;
    }
}
