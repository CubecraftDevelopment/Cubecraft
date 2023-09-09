package ink.flybird.quantum3d.drawcall;

import ink.flybird.quantum3d.draw.VertexBuilder;

public interface IRenderCall {
    static IRenderCall create(boolean useVBO) {
        if (useVBO) {
            return new VBORenderCall();
        } else {
            return new ListRenderCall();
        }
    }

    void call();

    void upload(VertexBuilder builder);

    void allocate();

    void free();

    boolean isAllocated();

    int getHandle();
}
