package me.gb2022.quantum3d.legacy.drawcall;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;

public interface IRenderCall {
    static IRenderCall create(boolean useVBO) {
        if (useVBO) {
            return new VBORenderCall();
        } else {
            return new ListRenderCall();
        }
    }

    void call();

    void upload(LegacyVertexBuilder builder);

    void allocate();

    void free();

    boolean isAllocated();

    int getHandle();
}
