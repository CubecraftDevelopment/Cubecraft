package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.compile.CompileCallable;

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
