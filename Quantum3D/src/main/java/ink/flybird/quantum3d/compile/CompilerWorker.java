package ink.flybird.quantum3d.compile;

import ink.flybird.quantum3d.BufferAllocation;
import ink.flybird.quantum3d.draw.DrawCompile;
import ink.flybird.quantum3d.draw.IDrawCompile;
import ink.flybird.fcommon.container.ArrayQueue;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;

import java.util.Set;

public class CompilerWorker<T extends CompileCallable<T>> implements Runnable {
    public final ArrayQueue<DrawCompile<T>> queue;
    public final ArrayQueue<IDrawCompile<T>> allQueue;
    public final ArrayQueue<? extends CompileCallable<T>> src;
    private final Logger logger = new SimpleLogger("CompileWorker");
    private final MultiRenderCompileService<?> parent;

    private boolean running = true;

    public CompilerWorker(ArrayQueue<DrawCompile<T>> queue, ArrayQueue<IDrawCompile<T>> allQueue, ArrayQueue<? extends CompileCallable<T>> src, MultiRenderCompileService<?> parent) {
        this.queue = queue;
        this.allQueue = allQueue;
        this.src = src;
        this.parent = parent;
    }

    @Override
    public void run() {
        while (this.running) {
            if (BufferAllocation.getAllocSize() > 67108864*8) {
                Thread.yield();
                continue;
            }
            try {
                CompileCallable<T> obj = this.src.poll();
                if (obj != null) {
                    if(!obj.shouldCompile()){
                        continue;
                    }
                    this.parent.activeWorkerCount.addAndGet(1);
                    Set<IDrawCompile<T>> res = obj.compile();
                    for (IDrawCompile<T> dc : res) {
                        if (dc instanceof DrawCompile) {
                            if(!this.running){
                                ((DrawCompile<T>) dc).free();
                                continue;
                            }
                            this.queue.add((DrawCompile<T>) dc);
                        }
                        this.allQueue.add(dc);
                    }
                    this.parent.activeWorkerCount.addAndGet(-1);
                    Thread.yield();
                } else {
                    Thread.yield();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.logger.exception(e);
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
