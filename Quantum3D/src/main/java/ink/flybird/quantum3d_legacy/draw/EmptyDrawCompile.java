package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.quantum3d_legacy.compile.CompileCallable;

public class EmptyDrawCompile<T extends CompileCallable> implements IDrawCompile<T>{
    private final T obj;

    public EmptyDrawCompile(T obj) {
        this.obj = obj;
    }

    @Override
    public T getObject() {
        return obj;
    }
}
