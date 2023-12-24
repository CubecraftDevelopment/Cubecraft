package me.gb2022.quantum3d.lwjgl.deprecated.pipeline;

public interface ShaderAllocator {
    static Shader allocateVertexShader() {
        return allocate(ShaderType.VERTEX);
    }

    static Shader allocateTessellateControlShader() {
        return allocate(ShaderType.TESSELLATE_CONTROL);
    }

    static Shader allocateTessellateEvaluationShader() {
        return allocate(ShaderType.TESSELLATE_EVALUATION);
    }

    static Shader allocateGeometryShader() {
        return allocate(ShaderType.GEOMETRY);
    }

    static Shader allocateFragmentShader() {
        return allocate(ShaderType.FRAGMENT);
    }

    static Shader allocate(ShaderType type) {
        Shader shader = new Shader(type);
        shader.allocate();
        return shader;
    }
}
