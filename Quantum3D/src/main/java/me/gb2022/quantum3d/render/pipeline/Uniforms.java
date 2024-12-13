package me.gb2022.quantum3d.render.pipeline;

import me.gb2022.quantum3d.util.camera.Camera;
import org.lwjgl.opengl.GL30;

public interface Uniforms {
    static void view(Program prog, float vx, float vy, float vz) {
        GL30.glUniform3f(prog.getUniformLocation("_view"), vx, vy, vz);
    }

    static void camProj(Program prog, Camera camera) {
        GL30.glUniformMatrix4fv(prog.getUniformLocation("_cam_proj"), false, camera.getProjectionMatrix().get(new float[16]));
    }
}
