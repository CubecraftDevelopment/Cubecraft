package net.cubecraft.client.control;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;

public interface InputCommand {
    // 数字键
    InputCommand KEY_0 = InputCommand.of(KeyboardButton.KEY_0);
    InputCommand KEY_1 = InputCommand.of(KeyboardButton.KEY_1);
    InputCommand KEY_2 = InputCommand.of(KeyboardButton.KEY_2);
    InputCommand KEY_3 = InputCommand.of(KeyboardButton.KEY_3);
    InputCommand KEY_4 = InputCommand.of(KeyboardButton.KEY_4);
    InputCommand KEY_5 = InputCommand.of(KeyboardButton.KEY_5);
    InputCommand KEY_6 = InputCommand.of(KeyboardButton.KEY_6);
    InputCommand KEY_7 = InputCommand.of(KeyboardButton.KEY_7);
    InputCommand KEY_8 = InputCommand.of(KeyboardButton.KEY_8);
    InputCommand KEY_9 = InputCommand.of(KeyboardButton.KEY_9);

    // 字母键
    InputCommand KEY_A = InputCommand.of(KeyboardButton.KEY_A);
    InputCommand KEY_B = InputCommand.of(KeyboardButton.KEY_B);
    InputCommand KEY_C = InputCommand.of(KeyboardButton.KEY_C);
    InputCommand KEY_D = InputCommand.of(KeyboardButton.KEY_D);
    InputCommand KEY_E = InputCommand.of(KeyboardButton.KEY_E);
    InputCommand KEY_F = InputCommand.of(KeyboardButton.KEY_F);
    InputCommand KEY_G = InputCommand.of(KeyboardButton.KEY_G);
    InputCommand KEY_H = InputCommand.of(KeyboardButton.KEY_H);
    InputCommand KEY_I = InputCommand.of(KeyboardButton.KEY_I);
    InputCommand KEY_J = InputCommand.of(KeyboardButton.KEY_J);
    InputCommand KEY_K = InputCommand.of(KeyboardButton.KEY_K);
    InputCommand KEY_L = InputCommand.of(KeyboardButton.KEY_L);
    InputCommand KEY_M = InputCommand.of(KeyboardButton.KEY_M);
    InputCommand KEY_N = InputCommand.of(KeyboardButton.KEY_N);
    InputCommand KEY_O = InputCommand.of(KeyboardButton.KEY_O);
    InputCommand KEY_P = InputCommand.of(KeyboardButton.KEY_P);
    InputCommand KEY_Q = InputCommand.of(KeyboardButton.KEY_Q);
    InputCommand KEY_R = InputCommand.of(KeyboardButton.KEY_R);
    InputCommand KEY_S = InputCommand.of(KeyboardButton.KEY_S);
    InputCommand KEY_T = InputCommand.of(KeyboardButton.KEY_T);
    InputCommand KEY_U = InputCommand.of(KeyboardButton.KEY_U);
    InputCommand KEY_V = InputCommand.of(KeyboardButton.KEY_V);
    InputCommand KEY_W = InputCommand.of(KeyboardButton.KEY_W);
    InputCommand KEY_X = InputCommand.of(KeyboardButton.KEY_X);
    InputCommand KEY_Y = InputCommand.of(KeyboardButton.KEY_Y);
    InputCommand KEY_Z = InputCommand.of(KeyboardButton.KEY_Z);

    // 功能键
    InputCommand KEY_ENTER = InputCommand.of(KeyboardButton.KEY_ENTER);
    InputCommand KEY_BACKSPACE = InputCommand.of(KeyboardButton.KEY_BACKSPACE);
    InputCommand KEY_TAB = InputCommand.of(KeyboardButton.KEY_TAB);
    InputCommand KEY_LEFT_SHIFT = InputCommand.of(KeyboardButton.KEY_LEFT_SHIFT);
    InputCommand KEY_RIGHT_SHIFT = InputCommand.of(KeyboardButton.KEY_RIGHT_SHIFT);
    InputCommand KEY_LEFT_CONTROL = InputCommand.of(KeyboardButton.KEY_LEFT_CONTROL);
    InputCommand KEY_RIGHT_CONTROL = InputCommand.of(KeyboardButton.KEY_RIGHT_CONTROL);
    InputCommand KEY_LEFT_ALT = InputCommand.of(KeyboardButton.KEY_LEFT_ALT);
    InputCommand KEY_RIGHT_ALT = InputCommand.of(KeyboardButton.KEY_RIGHT_ALT);
    InputCommand KEY_CAPS_LOCK = InputCommand.of(KeyboardButton.KEY_CAPS_LOCK);
    InputCommand KEY_ESC = InputCommand.of(KeyboardButton.KEY_ESCAPE);

    // 方向键
    InputCommand KEY_LEFT = InputCommand.of(KeyboardButton.KEY_LEFT);
    InputCommand KEY_RIGHT = InputCommand.of(KeyboardButton.KEY_RIGHT);
    InputCommand KEY_UP = InputCommand.of(KeyboardButton.KEY_UP);
    InputCommand KEY_DOWN = InputCommand.of(KeyboardButton.KEY_DOWN);

    // 特殊键
    InputCommand KEY_SPACE = InputCommand.of(KeyboardButton.KEY_SPACE);
    InputCommand KEY_COMMA = InputCommand.of(KeyboardButton.KEY_COMMA);
    InputCommand KEY_PERIOD = InputCommand.of(KeyboardButton.KEY_PERIOD);
    InputCommand KEY_SLASH = InputCommand.of(KeyboardButton.KEY_SLASH);
    InputCommand KEY_SEMICOLON = InputCommand.of(KeyboardButton.KEY_SEMICOLON);
    InputCommand KEY_QUOTE = InputCommand.of(KeyboardButton.KEY_COMMA);
    InputCommand KEY_LEFT_BRACKET = InputCommand.of(KeyboardButton.KEY_LEFT_BRACKET);
    InputCommand KEY_RIGHT_BRACKET = InputCommand.of(KeyboardButton.KEY_RIGHT_BRACKET);
    InputCommand KEY_BACKSLASH = InputCommand.of(KeyboardButton.KEY_BACKSLASH);

    // NUMPAD
    InputCommand KEY_NUMPAD_0 = InputCommand.of(KeyboardButton.KEY_NUMPAD_0);
    InputCommand KEY_NUMPAD_1 = InputCommand.of(KeyboardButton.KEY_NUMPAD_1);
    InputCommand KEY_NUMPAD_2 = InputCommand.of(KeyboardButton.KEY_NUMPAD_2);
    InputCommand KEY_NUMPAD_3 = InputCommand.of(KeyboardButton.KEY_NUMPAD_3);
    InputCommand KEY_NUMPAD_4 = InputCommand.of(KeyboardButton.KEY_NUMPAD_4);
    InputCommand KEY_NUMPAD_5 = InputCommand.of(KeyboardButton.KEY_NUMPAD_5);
    InputCommand KEY_NUMPAD_6 = InputCommand.of(KeyboardButton.KEY_NUMPAD_6);
    InputCommand KEY_NUMPAD_7 = InputCommand.of(KeyboardButton.KEY_NUMPAD_7);
    InputCommand KEY_NUMPAD_8 = InputCommand.of(KeyboardButton.KEY_NUMPAD_8);
    InputCommand KEY_NUMPAD_9 = InputCommand.of(KeyboardButton.KEY_NUMPAD_9);
    InputCommand KEY_NUMPAD_DIVIDE = InputCommand.of(KeyboardButton.KEY_NUMPAD_DIVIDE);
    InputCommand KEY_NUMPAD_MULTIPLY = InputCommand.of(KeyboardButton.KEY_NUMPAD_MULTIPLY);
    InputCommand KEY_NUMPAD_SUBTRACT = InputCommand.of(KeyboardButton.KEY_NUMPAD_SUBTRACT);
    InputCommand KEY_NUMPAD_ADD = InputCommand.of(KeyboardButton.KEY_NUMPAD_ADD);
    InputCommand KEY_NUMPAD_ENTER = InputCommand.of(KeyboardButton.KEY_NUMPAD_ENTER);
    InputCommand KEY_NUMPAD_DECIMAL = InputCommand.of(KeyboardButton.KEY_NUMPAD_DECIMAL);

    // System
    InputCommand KEY_F1 = InputCommand.of(KeyboardButton.KEY_F1);
    InputCommand KEY_F2 = InputCommand.of(KeyboardButton.KEY_F2);
    InputCommand KEY_F3 = InputCommand.of(KeyboardButton.KEY_F3);
    InputCommand KEY_F4 = InputCommand.of(KeyboardButton.KEY_F4);
    InputCommand KEY_F5 = InputCommand.of(KeyboardButton.KEY_F5);
    InputCommand KEY_F6 = InputCommand.of(KeyboardButton.KEY_F6);
    InputCommand KEY_F7 = InputCommand.of(KeyboardButton.KEY_F7);
    InputCommand KEY_F8 = InputCommand.of(KeyboardButton.KEY_F8);
    InputCommand KEY_F9 = InputCommand.of(KeyboardButton.KEY_F9);
    InputCommand KEY_F10 = InputCommand.of(KeyboardButton.KEY_F10);
    InputCommand KEY_F11 = InputCommand.of(KeyboardButton.KEY_F11);
    InputCommand KEY_F12 = InputCommand.of(KeyboardButton.KEY_F12);

    // Windows
    InputCommand KEY_MENU = InputCommand.of(KeyboardButton.KEY_MENU);

    // Mouse
    InputCommand MOUSE_BUTTON_LEFT = InputCommand.of(MouseButton.MOUSE_BUTTON_LEFT);
    InputCommand MOUSE_BUTTON_RIGHT = InputCommand.of(MouseButton.MOUSE_BUTTON_RIGHT);
    InputCommand MOUSE_BUTTON_MIDDLE = InputCommand.of(MouseButton.MOUSE_BUTTON_MIDDLE);


    static InputCommand of(MouseButton button) {
        return new MouseButtonComponent(button);
    }

    static InputCommand of(KeyboardButton button) {
        return new KeyboardComponent(button);
    }

    static InputCommand of(String s) {
        try {
            return (InputCommand) InputCommand.class.getDeclaredField(s).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static String serialize(InputCommand command) {
        if (command instanceof MouseButtonComponent c) {
            return c.component.name();
        }
        if (command instanceof KeyboardComponent c) {
            return c.component.name();
        }

        throw new IllegalArgumentException("commands");
    }

    boolean isActive(Keyboard keyboard, Mouse mouse);

    boolean isTriggered(KeyboardButton kb, MouseButton button);

    final class KeyboardComponent implements InputCommand {
        private final KeyboardButton component;

        public KeyboardComponent(KeyboardButton component) {
            this.component = component;
        }

        @Override
        public boolean isActive(Keyboard keyboard, Mouse mouse) {
            if (keyboard == null) {
                return false;
            }
            return keyboard.isKeyDown(this.component);
        }

        @Override
        public boolean isTriggered(KeyboardButton kb, MouseButton button) {
            return kb == this.component;
        }
    }

    final class MouseButtonComponent implements InputCommand {
        private final MouseButton component;

        public MouseButtonComponent(MouseButton component) {
            this.component = component;
        }

        @Override
        public boolean isActive(Keyboard keyboard, Mouse mouse) {
            if (mouse == null) {
                return false;
            }
            return mouse.isButtonDown(this.component);
        }

        @Override
        public boolean isTriggered(KeyboardButton kb, MouseButton button) {
            return button == this.component;
        }
    }
}
