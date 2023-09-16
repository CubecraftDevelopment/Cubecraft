package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.compile.CompileCallable;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;

public final class DrawCompile<T extends CompileCallable<T>> implements Comparable<DrawCompile<T>>, IDrawCompile<T> {
    private final IRenderCall call;
    private final VertexBuilder builder;
    private final T obj;

    public DrawCompile(IRenderCall call, VertexBuilder builder, T obj) {
        this.call = call;
        this.builder = builder;
        this.obj = obj;
    }

    @Override
    public int compareTo(DrawCompile<T> o) {
        return 0;
    }

    public void draw() {
        GLUtil.assertRenderThread();
        this.call.upload(this.builder);
        this.builder.free();
    }

    @Override
    public T getObject() {
        return obj;
    }

    public void free() {
        this.builder.free();
    }
}
