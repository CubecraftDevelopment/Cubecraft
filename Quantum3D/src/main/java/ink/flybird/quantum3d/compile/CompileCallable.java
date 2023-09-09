package ink.flybird.quantum3d.compile;

import ink.flybird.quantum3d.draw.IDrawCompile;

import java.util.Set;

public interface CompileCallable<T extends CompileCallable<T>> {
    Set<IDrawCompile<T>> compile();

    //default void allocate(){}

    default void destroy(){}

    boolean shouldCompile();
}
