package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.IWorld;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.BufferAllocation;

import java.util.PriorityQueue;

public abstract class ChunkCompilerTask implements Runnable {
    private static final ILogger LOGGER = LogManager.getLogger("chunk-compiler-task");

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
                    LOGGER.error(e);
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
