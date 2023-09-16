package ink.flybird.quantum3d_legacy.compile;

import ink.flybird.quantum3d_legacy.draw.IDrawCompile;
import ink.flybird.quantum3d_legacy.draw.DrawCompile;
import ink.flybird.fcommon.container.ArrayQueue;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiRenderCompileService<V extends CompileCallable<V>> implements IDrawService<V> {
    private static final ThreadFactory THREAD_FACTORY = r -> {
        Thread t=new Thread(r);
        t.setDaemon(true);
        return t;
    };


    private final ArrayQueue<DrawCompile<V>> multiDrawResult = new ArrayQueue<>();
    private final ArrayQueue<IDrawCompile<V>> all = new ArrayQueue<>();
    private final ArrayQueue<V> cache = new ArrayQueue<>();
    private final CompilerWorker<V>[] workers;
    protected final AtomicInteger activeWorkerCount = new AtomicInteger(0);
    private boolean active=true;

    public MultiRenderCompileService(int max) {
        workers = new CompilerWorker[max];
        for (int i = 0; i < max; i++) {
            CompilerWorker<V> worker = new CompilerWorker<>(this.multiDrawResult, this.all, this.cache, this);
            workers[i] = worker;
            THREAD_FACTORY.newThread(worker).start();
        }
    }

    @Override
    public void startDrawing(V v) {
        if(!this.active){
            return;
        }
        this.cache.add(v);
    }

    @Override
    public void startDirect(V v) {
        if(!this.active){
            return;
        }
        if (this.cache.contains(v)) {
            this.cache.remove(v);
            new Thread(new VertexArrayCompilerCall<>(v, this.multiDrawResult, this.all)).start();
        } else {
            this.startDrawing(v);
        }
    }

    @Override
    public int getResultSize() {
        return this.multiDrawResult.size();
    }

    @Override
    public DrawCompile<V> getAvailableCompile() {
        return this.multiDrawResult.poll();
    }

    @Override
    public IDrawCompile<V> getAllCompile() {
        return this.all.poll();
    }

    @Override
    public int getAllResultSize() {
        return this.all.size();
    }

    @Override
    public ArrayQueue<V> getCache() {
        return this.cache;
    }

    public void stop() {
        this.active=false;
        for (CompilerWorker<V> w : this.workers) {
            w.setRunning(false);
        }
        this.multiDrawResult.clear();
        this.all.clear();
        this.cache.clear();
    }

    @Override
    public int getActiveWorkerCount() {
        return activeWorkerCount.get();
    }
}
