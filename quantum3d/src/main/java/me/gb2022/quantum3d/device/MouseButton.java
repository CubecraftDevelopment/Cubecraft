package me.gb2022.quantum3d.device;

/**
 * The enum MouseButton represents different mouse buttons along with their corresponding codes.
 * It provides a mapping of button codes to MouseButton instances.
 */
public enum MouseButton {

    // Mouse button constants along with their codes
    UNKNOWN_BUTTON(-1),
    MOUSE_BUTTON_LEFT(0),
    MOUSE_BUTTON_RIGHT(1),
    MOUSE_BUTTON_MIDDLE(2),
    MOUSE_BUTTON_4(3),
    MOUSE_BUTTON_5(4),
    MOUSE_BUTTON_6(5),
    MOUSE_BUTTON_7(6),
    MOUSE_BUTTON_8(7);

    private final int code;

    /**
     * Constructs a MouseButton with the specified button code.
     *
     * @param code The code associated with this MouseButton.
     */
    MouseButton(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * Get the MouseButton instance corresponding to the given button code.
     *
     * @param value The button code to look up.
     * @return The corresponding MouseButton instance.
     */
    public static MouseButton of(int value) {
        return switch (value) {
            case 0 -> MOUSE_BUTTON_LEFT;
            case 1 -> MOUSE_BUTTON_RIGHT;
            case 2 -> MOUSE_BUTTON_MIDDLE;
            case 3 -> MOUSE_BUTTON_4;
            case 4 -> MOUSE_BUTTON_5;
            case 5 -> MOUSE_BUTTON_6;
            case 6 -> MOUSE_BUTTON_7;
            case 7 -> MOUSE_BUTTON_8;
            default -> UNKNOWN_BUTTON;
        };
    }
}
