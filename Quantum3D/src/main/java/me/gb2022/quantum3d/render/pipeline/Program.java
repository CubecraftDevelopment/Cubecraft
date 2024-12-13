package me.gb2022.quantum3d.render.pipeline;

import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;

public final class Program {
    private Map<String, Integer> uniformLocations = new HashMap<String, Integer>();
    private int handle = -1;

    public void allocate() {
        this.handle = GL30.glCreateProgram();
    }

    public void destroy() {
        GL30.glDeleteProgram(this.handle);
        this.handle = -1;
    }

    public void addShader(Shader shader) {
        GL30.glAttachShader(this.handle, shader.getGlHandle());
    }

    public void link() {
        GL30.glLinkProgram(this.handle);
    }

    public void bind() {
        GL30.glUseProgram(this.handle);
    }

    public int getHandle() {
        return handle;
    }

    public int getUniformLocation(String name) {
        return this.uniformLocations.computeIfAbsent(name, k -> GL30.glGetUniformLocation(this.handle, k));
    }
}
