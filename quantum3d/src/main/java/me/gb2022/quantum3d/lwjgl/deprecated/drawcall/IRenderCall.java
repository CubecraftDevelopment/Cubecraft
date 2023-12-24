package me.gb2022.quantum3d.lwjgl.deprecated.drawcall;

import me.gb2022.quantum3d.lwjgl.deprecated.drawcall.ListRenderCall;
import me.gb2022.quantum3d.lwjgl.deprecated.drawcall.VBORenderCall;
import me.gb2022.quantum3d.lwjgl.vertex.LocalVertexBuilder;

public interface IRenderCall {
    static IRenderCall create(boolean useVBO) {
        if (useVBO) {
            return new VBORenderCall();
        } else {
            return new ListRenderCall();
        }
    }

    void call();

    void upload(LocalVertexBuilder builder);

    void allocate();

    void free();

    boolean isAllocated();

    int getHandle();
}
