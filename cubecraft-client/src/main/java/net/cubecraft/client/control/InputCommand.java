package net.cubecraft.client.control;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;

public interface InputCommand {
    // 数字键
    InputCommand KEYBOARD_0 = InputCommand.of(KeyboardButton.KEY_0);
    InputCommand KEYBOARD_1 = InputCommand.of(KeyboardButton.KEY_1);
    InputCommand KEYBOARD_2 = InputCommand.of(KeyboardButton.KEY_2);
    InputCommand KEYBOARD_3 = InputCommand.of(KeyboardButton.KEY_3);
    InputCommand KEYBOARD_4 = InputCommand.of(KeyboardButton.KEY_4);
    InputCommand KEYBOARD_5 = InputCommand.of(KeyboardButton.KEY_5);
    InputCommand KEYBOARD_6 = InputCommand.of(KeyboardButton.KEY_6);
    InputCommand KEYBOARD_7 = InputCommand.of(KeyboardButton.KEY_7);
    InputCommand KEYBOARD_8 = InputCommand.of(KeyboardButton.KEY_8);
    InputCommand KEYBOARD_9 = InputCommand.of(KeyboardButton.KEY_9);

    // 字母键
    InputCommand KEYBOARD_A = InputCommand.of(KeyboardButton.KEY_A);
    InputCommand KEYBOARD_B = InputCommand.of(KeyboardButton.KEY_B);
    InputCommand KEYBOARD_C = InputCommand.of(KeyboardButton.KEY_C);
    InputCommand KEYBOARD_D = InputCommand.of(KeyboardButton.KEY_D);
    InputCommand KEYBOARD_E = InputCommand.of(KeyboardButton.KEY_E);
    InputCommand KEYBOARD_F = InputCommand.of(KeyboardButton.KEY_F);
    InputCommand KEYBOARD_G = InputCommand.of(KeyboardButton.KEY_G);
    InputCommand KEYBOARD_H = InputCommand.of(KeyboardButton.KEY_H);
    InputCommand KEYBOARD_I = InputCommand.of(KeyboardButton.KEY_I);
    InputCommand KEYBOARD_J = InputCommand.of(KeyboardButton.KEY_J);
    InputCommand KEYBOARD_K = InputCommand.of(KeyboardButton.KEY_K);
    InputCommand KEYBOARD_L = InputCommand.of(KeyboardButton.KEY_L);
    InputCommand KEYBOARD_M = InputCommand.of(KeyboardButton.KEY_M);
    InputCommand KEYBOARD_N = InputCommand.of(KeyboardButton.KEY_N);
    InputCommand KEYBOARD_O = InputCommand.of(KeyboardButton.KEY_O);
    InputCommand KEYBOARD_P = InputCommand.of(KeyboardButton.KEY_P);
    InputCommand KEYBOARD_Q = InputCommand.of(KeyboardButton.KEY_Q);
    InputCommand KEYBOARD_R = InputCommand.of(KeyboardButton.KEY_R);
    InputCommand KEYBOARD_S = InputCommand.of(KeyboardButton.KEY_S);
    InputCommand KEYBOARD_T = InputCommand.of(KeyboardButton.KEY_T);
    InputCommand KEYBOARD_U = InputCommand.of(KeyboardButton.KEY_U);
    InputCommand KEYBOARD_V = InputCommand.of(KeyboardButton.KEY_V);
    InputCommand KEYBOARD_W = InputCommand.of(KeyboardButton.KEY_W);
    InputCommand KEYBOARD_X = InputCommand.of(KeyboardButton.KEY_X);
    InputCommand KEYBOARD_Y = InputCommand.of(KeyboardButton.KEY_Y);
    InputCommand KEYBOARD_Z = InputCommand.of(KeyboardButton.KEY_Z);

    // 功能键
    InputCommand KEYBOARD_ENTER = InputCommand.of(KeyboardButton.KEY_ENTER);
    InputCommand KEYBOARD_BACKSPACE = InputCommand.of(KeyboardButton.KEY_BACKSPACE);
    InputCommand KEYBOARD_TAB = InputCommand.of(KeyboardButton.KEY_TAB);
    InputCommand KEYBOARD_LEFT_SHIFT = InputCommand.of(KeyboardButton.KEY_LEFT_SHIFT);
    InputCommand KEYBOARD_RIGHT_SHIFT = InputCommand.of(KeyboardButton.KEY_RIGHT_SHIFT);
    InputCommand KEYBOARD_LEFT_CONTROL = InputCommand.of(KeyboardButton.KEY_LEFT_CONTROL);
    InputCommand KEYBOARD_RIGHT_CONTROL = InputCommand.of(KeyboardButton.KEY_RIGHT_CONTROL);
    InputCommand KEYBOARD_LEFT_ALT = InputCommand.of(KeyboardButton.KEY_LEFT_ALT);
    InputCommand KEYBOARD_RIGHT_ALT = InputCommand.of(KeyboardButton.KEY_RIGHT_ALT);
    InputCommand KEYBOARD_CAPS_LOCK = InputCommand.of(KeyboardButton.KEY_CAPS_LOCK);
    InputCommand KEYBOARD_ESC = InputCommand.of(KeyboardButton.KEY_ESCAPE);

    // 方向键
    InputCommand KEYBOARD_LEFT = InputCommand.of(KeyboardButton.KEY_LEFT);
    InputCommand KEYBOARD_RIGHT = InputCommand.of(KeyboardButton.KEY_RIGHT);
    InputCommand KEYBOARD_UP = InputCommand.of(KeyboardButton.KEY_UP);
    InputCommand KEYBOARD_DOWN = InputCommand.of(KeyboardButton.KEY_DOWN);

    // 特殊键
    InputCommand KEYBOARD_SPACE = InputCommand.of(KeyboardButton.KEY_SPACE);
    InputCommand KEYBOARD_COMMA = InputCommand.of(KeyboardButton.KEY_COMMA);
    InputCommand KEYBOARD_PERIOD = InputCommand.of(KeyboardButton.KEY_PERIOD);
    InputCommand KEYBOARD_SLASH = InputCommand.of(KeyboardButton.KEY_SLASH);
    InputCommand KEYBOARD_SEMICOLON = InputCommand.of(KeyboardButton.KEY_SEMICOLON);
    InputCommand KEYBOARD_QUOTE = InputCommand.of(KeyboardButton.KEY_COMMA);
    InputCommand KEYBOARD_LEFT_BRACKET = InputCommand.of(KeyboardButton.KEY_LEFT_BRACKET);
    InputCommand KEYBOARD_RIGHT_BRACKET = InputCommand.of(KeyboardButton.KEY_RIGHT_BRACKET);
    InputCommand KEYBOARD_BACKSLASH = InputCommand.of(KeyboardButton.KEY_BACKSLASH);

    // NUMPAD
    InputCommand KEYBOARD_NUMPAD_0 = InputCommand.of(KeyboardButton.KEY_NUMPAD_0);
    InputCommand KEYBOARD_NUMPAD_1 = InputCommand.of(KeyboardButton.KEY_NUMPAD_1);
    InputCommand KEYBOARD_NUMPAD_2 = InputCommand.of(KeyboardButton.KEY_NUMPAD_2);
    InputCommand KEYBOARD_NUMPAD_3 = InputCommand.of(KeyboardButton.KEY_NUMPAD_3);
    InputCommand KEYBOARD_NUMPAD_4 = InputCommand.of(KeyboardButton.KEY_NUMPAD_4);
    InputCommand KEYBOARD_NUMPAD_5 = InputCommand.of(KeyboardButton.KEY_NUMPAD_5);
    InputCommand KEYBOARD_NUMPAD_6 = InputCommand.of(KeyboardButton.KEY_NUMPAD_6);
    InputCommand KEYBOARD_NUMPAD_7 = InputCommand.of(KeyboardButton.KEY_NUMPAD_7);
    InputCommand KEYBOARD_NUMPAD_8 = InputCommand.of(KeyboardButton.KEY_NUMPAD_8);
    InputCommand KEYBOARD_NUMPAD_9 = InputCommand.of(KeyboardButton.KEY_NUMPAD_9);
    InputCommand KEYBOARD_NUMPAD_DIVIDE = InputCommand.of(KeyboardButton.KEY_NUMPAD_DIVIDE);
    InputCommand KEYBOARD_NUMPAD_MULTIPLY = InputCommand.of(KeyboardButton.KEY_NUMPAD_MULTIPLY);
    InputCommand KEYBOARD_NUMPAD_SUBTRACT = InputCommand.of(KeyboardButton.KEY_NUMPAD_SUBTRACT);
    InputCommand KEYBOARD_NUMPAD_ADD = InputCommand.of(KeyboardButton.KEY_NUMPAD_ADD);
    InputCommand KEYBOARD_NUMPAD_ENTER = InputCommand.of(KeyboardButton.KEY_NUMPAD_ENTER);
    InputCommand KEYBOARD_NUMPAD_DECIMAL = InputCommand.of(KeyboardButton.KEY_NUMPAD_DECIMAL);

    // System
    InputCommand KEYBOARD_F1 = InputCommand.of(KeyboardButton.KEY_F1);
    InputCommand KEYBOARD_F2 = InputCommand.of(KeyboardButton.KEY_F2);
    InputCommand KEYBOARD_F3 = InputCommand.of(KeyboardButton.KEY_F3);
    InputCommand KEYBOARD_F4 = InputCommand.of(KeyboardButton.KEY_F4);
    InputCommand KEYBOARD_F5 = InputCommand.of(KeyboardButton.KEY_F5);
    InputCommand KEYBOARD_F6 = InputCommand.of(KeyboardButton.KEY_F6);
    InputCommand KEYBOARD_F7 = InputCommand.of(KeyboardButton.KEY_F7);
    InputCommand KEYBOARD_F8 = InputCommand.of(KeyboardButton.KEY_F8);
    InputCommand KEYBOARD_F9 = InputCommand.of(KeyboardButton.KEY_F9);
    InputCommand KEYBOARD_F10 = InputCommand.of(KeyboardButton.KEY_F10);
    InputCommand KEYBOARD_F11 = InputCommand.of(KeyboardButton.KEY_F11);
    InputCommand KEYBOARD_F12 = InputCommand.of(KeyboardButton.KEY_F12);

    // Windows
    InputCommand KEYBOARD_MENU = InputCommand.of(KeyboardButton.KEY_MENU);

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
