package ink.flybird.quantum3d_legacy.compile;

import ink.flybird.quantum3d_legacy.draw.DrawCompile;
import ink.flybird.quantum3d_legacy.draw.IDrawCompile;
import ink.flybird.fcommon.container.ArrayQueue;

public interface IDrawService<V extends CompileCallable<V>> {
    void startDrawing(V v);

    void startDirect(V v);

    int getResultSize();

    DrawCompile<V> getAvailableCompile();

    IDrawCompile<V> getAllCompile();

    int getAllResultSize();

    ArrayQueue<V> getCache();

    int getActiveWorkerCount();

    default void stop() {

    }
}
