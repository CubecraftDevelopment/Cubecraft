package ink.flybird.quantum3d.draw;

import org.lwjgl.opengl.GL11;

public enum DrawMode {
    POINTS(GL11.GL_POINTS),
    LINES(GL11.GL_LINES),
    LINE_STRIP(GL11.GL_LINE_STRIP),
    TRIANGLES(GL11.GL_TRIANGLES),
    TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
    QUADS(GL11.GL_QUADS),
    QUAD_STRIP(GL11.GL_QUAD_STRIP);

    final int glMode;

    DrawMode(int glMode) {
        this.glMode = glMode;
    }

    public int getGlMode() {
        return glMode;
    }
}
