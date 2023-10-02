package ink.flybird.cubecraft.client.render.chunk.compile;

import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.quantum3d_legacy.BufferAllocation;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ChunkCompilerTask implements Runnable {
    private static final Logger LOGGER = SharedContext.LOG_CONTEXT.createLogger("chunk_compiler_task");
    protected final PriorityQueue<ChunkCompileResult> resultQueue;
    protected final PriorityQueue<ChunkCompileRequest> requestQueue;

    private boolean running = true;

    protected ChunkCompilerTask(PriorityQueue<ChunkCompileRequest> request, PriorityQueue<ChunkCompileResult> result) {
        this.resultQueue = result;
        this.requestQueue = request;
    }

    public static ChunkCompilerTask daemon(PriorityQueue<ChunkCompileRequest> request, PriorityQueue<ChunkCompileResult> result) {
        return new CompilerDaemon(request, result);
    }

    public static ChunkCompilerTask task(PriorityQueue<ChunkCompileRequest> request, PriorityQueue<ChunkCompileResult> result) {
        return new CompilerTask(request, result);
    }

    protected void process() {
        while (BufferAllocation.getAllocSize() > 67108864 * 8) {
            Thread.yield();
        }
        try {
            ChunkCompileRequest request;
            synchronized ("chunk_poll") {
                if (this.requestQueue.isEmpty()) {
                    return;
                }
                request = this.requestQueue.poll();
            }

            if (request == null) {
                return;
            }
            IWorld world = request.getWorld();
            RenderChunkPos pos = request.getPos();
            String layerId = request.getLayerId();

            ChunkCompileResult result;

            if (request.getLayer() == null) {
                result = ChunkCompiler.build(layerId, world, pos);
            } else {
                result = ChunkCompiler.rebuild(layerId, world, pos, request.getLayer());
            }

            if ((!this.running) && result.isSuccess()) {
                result.getBuilder().free();
                return;
            }
            this.resultQueue.add(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    private static class CompilerDaemon extends ChunkCompilerTask {
        protected CompilerDaemon(PriorityQueue<ChunkCompileRequest> request, PriorityQueue<ChunkCompileResult> result) {
            super(request, result);
        }

        @Override
        public void run() {
            while (this.isRunning()) {
                try {
                    this.process();
                } catch (Exception e) {
                    LOGGER.exception(e);
                }
                Thread.yield();
            }
        }
    }

    private static class CompilerTask extends ChunkCompilerTask {
        protected CompilerTask(PriorityQueue<ChunkCompileRequest> request, PriorityQueue<ChunkCompileResult> result) {
            super(request, result);
        }

        @Override
        public void run() {
            this.process();
        }
    }
}
