package me.gb2022.quantum3d.render.vertex;

import org.lwjgl.opengl.GL11;

public enum DrawMode {
    POINTS,
    LINES,
    LINE_STRIP,
    TRIANGLES,
    TRIANGLE_FAN,
    QUADS,
    QUAD_STRIP;

    public int glId() {
        return switch (this) {
            case LINES -> GL11.GL_LINES;
            case QUADS -> GL11.GL_QUADS;
            case QUAD_STRIP -> GL11.GL_QUAD_STRIP;
            case POINTS -> GL11.GL_POINTS;
            case TRIANGLE_FAN -> GL11.GL_TRIANGLE_FAN;
            case TRIANGLES -> GL11.GL_TRIANGLES;
            case LINE_STRIP -> GL11.GL_LINE_STRIP;
        };
    }
}
