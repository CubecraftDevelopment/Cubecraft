package ink.flybird.quantum3d.pipeline;

import org.lwjgl.opengl.*;

public enum ShaderType {
    VERTEX(GL20.GL_VERTEX_SHADER),
    TESSELLATE_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
    TESSELLATE_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
    GEOMETRY(GL40.GL_GEOMETRY_SHADER),
    FRAGMENT(GL20.GL_FRAGMENT_SHADER);

    final int glID;

    ShaderType(int glID) {
        this.glID = glID;
    }
}
