package net.cubecraft.client.gui.font;

public enum FontAlignment {
    LEFT,
    MIDDLE,
    RIGHT;

    public static FontAlignment from(String alignment) {
        return switch (alignment){
            case "middle"->MIDDLE;
            case "right"->RIGHT;
            default -> LEFT;
        };
    }
}
