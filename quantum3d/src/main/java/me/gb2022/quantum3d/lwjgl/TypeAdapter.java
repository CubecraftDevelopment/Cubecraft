package me.gb2022.quantum3d.lwjgl;

import me.gb2022.quantum3d.render.vertex.DataType;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import org.lwjgl.opengl.GL11;

public interface TypeAdapter {
    static int DrawModeToGLID(DrawMode mode) {
        return switch (mode) {
            case LINES -> GL11.GL_LINES;
            case QUADS -> GL11.GL_QUADS;
            case QUAD_STRIP -> GL11.GL_QUAD_STRIP;
            case POINTS -> GL11.GL_POINTS;
            case TRIANGLE_FAN -> GL11.GL_TRIANGLE_FAN;
            case TRIANGLES -> GL11.GL_TRIANGLES;
            case LINE_STRIP -> GL11.GL_LINE_STRIP;
        };
    }

    static int dataTypeToGLID(DataType type) {
        return switch (type) {
            case BYTE -> GL11.GL_BYTE;
            case SHORT -> GL11.GL_SHORT;
            case FLOAT -> GL11.GL_FLOAT;
            case DOUBLE -> GL11.GL_DOUBLE;
            case INTEGER -> GL11.GL_INT;
            case UNSIGNED_INT -> GL11.GL_UNSIGNED_INT;
            case UNSIGNED_BYTE -> GL11.GL_UNSIGNED_BYTE;
            case UNSIGNED_SHORT -> GL11.GL_UNSIGNED_SHORT;
            default -> throw new RuntimeException("wtf is this: " + type.name());
        };
    }
}
