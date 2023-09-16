package ink.flybird.quantum3d_legacy.draw;

import ink.flybird.quantum3d_legacy.compile.CompileCallable;

public interface IDrawCompile<T extends CompileCallable> {
    T getObject();
}
