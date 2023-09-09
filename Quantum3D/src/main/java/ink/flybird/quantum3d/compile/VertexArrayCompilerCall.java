package ink.flybird.quantum3d.compile;

import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.quantum3d.draw.DrawCompile;
import ink.flybird.quantum3d.draw.IDrawCompile;
import ink.flybird.fcommon.container.ArrayQueue;

import java.util.Set;


public class VertexArrayCompilerCall<T extends CompileCallable<T>> implements Runnable {
    private final Logger logger= new SimpleLogger("compiler_call");

    public final T compileCallable;
    public final ArrayQueue<DrawCompile<T>> queue;
    public final ArrayQueue<IDrawCompile<T>> allQueue;

    public VertexArrayCompilerCall(T compileCallable, ArrayQueue<DrawCompile<T>> queue, ArrayQueue<IDrawCompile<T>> allQueue) {
        this.compileCallable = compileCallable;
        this.queue = queue;
        this.allQueue = allQueue;
    }

    @Override
    public void run() {
        try {
            Set<IDrawCompile<T>> res = this.compileCallable.compile();
            for (IDrawCompile<T> dc : res) {
                if (dc instanceof DrawCompile) {
                    this.queue.add((DrawCompile<T>) dc);
                }
                this.allQueue.add(dc);
            }
        }catch (Exception e){
            this.logger.exception(e);
        }
    }
}
