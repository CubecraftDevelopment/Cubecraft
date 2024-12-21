package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Stack;

public class PoseStack {
    private final Stack<Matrix4f> matrixStack = new Stack<>();
    private final Matrix4f current = new Matrix4f(); // 当前模型矩阵

    public PoseStack() {
        this.current.identity(); // 初始化矩阵为单位矩阵
    }

    public void pushMatrix() {
        this.matrixStack.push(new Matrix4f(this.current)); // 将当前矩阵推入堆栈
    }

    public void popMatrix() {
        if (!this.matrixStack.isEmpty()) {
            this.current.set(this.matrixStack.pop()); // 恢复之前的矩阵
        }
    }

    public void translate(float x, float y, float z) {
        this.current.translate(x, y, z);
    }

    public void translate(Vector3f translation) {
        this.translate(translation.x, translation.y, translation.z);
    }

    public void rotate(float angle, float x, float y, float z) {
        this.current.rotate((float)Math.toRadians(angle), x, y, z);
    }

    public Matrix4f getMatrix() {
        return this.current;
    }
}
