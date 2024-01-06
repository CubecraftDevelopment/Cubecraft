package me.gb2022.quantum3d.render.vertex;

import org.lwjgl.opengl.GL11;

public enum DataType {
    BYTE, UNSIGNED_BYTE,
    SHORT, UNSIGNED_SHORT,
    INTEGER, UNSIGNED_INT,
    LONG, UNSIGNED_LONG,
    FLOAT,
    DOUBLE;

    public int getGlId() {
        return switch (this) {
            case BYTE -> GL11.GL_BYTE;
            case SHORT -> GL11.GL_SHORT;
            case FLOAT -> GL11.GL_FLOAT;
            case DOUBLE -> GL11.GL_DOUBLE;
            case INTEGER -> GL11.GL_INT;
            case UNSIGNED_INT -> GL11.GL_UNSIGNED_INT;
            case UNSIGNED_BYTE -> GL11.GL_UNSIGNED_BYTE;
            case UNSIGNED_SHORT -> GL11.GL_UNSIGNED_SHORT;
            default -> throw new RuntimeException("wtf is this: " + this.name());
        };
    }

    public int getBytes() {
        return switch (this) {
            case BYTE, UNSIGNED_BYTE -> 1;
            case SHORT, UNSIGNED_SHORT -> 2;
            case INTEGER, UNSIGNED_INT, FLOAT -> 4;
            case LONG, UNSIGNED_LONG, DOUBLE -> 8;
        };
    }
}
