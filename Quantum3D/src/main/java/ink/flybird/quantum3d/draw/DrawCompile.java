package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.compile.CompileCallable;
import ink.flybird.quantum3d.drawcall.IRenderCall;
import ink.flybird.quantum3d.GLUtil;
import org.jetbrains.annotations.NotNull;

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
    public int compareTo(@NotNull DrawCompile<T> o) {
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
