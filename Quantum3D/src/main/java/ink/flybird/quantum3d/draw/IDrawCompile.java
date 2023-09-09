package ink.flybird.quantum3d.draw;

import ink.flybird.quantum3d.compile.CompileCallable;

public interface IDrawCompile<T extends CompileCallable> {
    T getObject();
}
