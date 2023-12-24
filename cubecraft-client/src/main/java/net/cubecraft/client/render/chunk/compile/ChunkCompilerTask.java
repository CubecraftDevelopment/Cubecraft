package net.cubecraft.client.render.chunk.compile;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.BufferAllocation;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.IWorld;

import java.util.PriorityQueue;
import java.util.Queue;

public abstract class ChunkCompilerTask implements Runnable {
    private static final ILogger LOGGER = LogManager.getLogger("chunk-compiler-task");

    protected final Queue<ChunkCompileResult> resultQueue;
    protected final Queue<ChunkCompileRequest> requestQueue;

    private boolean running = true;

    protected ChunkCompilerTask(Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
        this.resultQueue = result;
        this.requestQueue = request;
    }

    public static ChunkCompilerTask daemon(Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
        return new CompilerDaemon(request, result);
    }

    public static ChunkCompilerTask task(ChunkCompileRequest request, Queue<ChunkCompileResult> result) {
        return new CompilerTask(request, result);
    }

    public void processRequest(ChunkCompileRequest request) {
        IWorld world = request.getWorld();
        RenderChunkPos pos = request.getPos();
        String layerId = request.getLayerId();

        ChunkCompileResult result;

        if (request.getLayer() == null) {
            result = ChunkCompiler.build(layerId, world, pos);
        } else {
            result = ChunkCompiler.rebuild(layerId, world, pos, request.getLayer());
        }
        if (result == null) {
            return;
        }

        if ((!this.running) && result.isSuccess()) {
            result.getBuilder().free();
            return;
        }
        this.resultQueue.add(result);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    private static class CompilerTask extends ChunkCompilerTask {
        private final ChunkCompileRequest request;

        protected CompilerTask(ChunkCompileRequest request, Queue<ChunkCompileResult> result) {
            super(null, result);
            this.request = request;
        }

        @Override
        public void run() {
            if (this.request == null) {
                return;
            }
            this.processRequest(this.request);
        }
    }

    private static class CompilerDaemon extends ChunkCompilerTask {
        protected CompilerDaemon(Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
            super(request, result);
        }

        @Override
        public void run() {
            while (this.isRunning()) {
                try {
                    if (this.requestQueue.isEmpty()) {
                        Thread.sleep(200);
                        Thread.yield();
                        continue;
                    }
                    boolean finished = false;
                    while (BufferAllocation.getAllocSize() > 67108864 * 8) {
                        Thread.yield();
                    }
                    try {
                        ChunkCompileRequest request = null;
                        synchronized ("chunk_poll") {
                            if (this.requestQueue.isEmpty()) {
                                Thread.yield();
                                finished = true;
                            } else {
                                request = this.requestQueue.poll();
                            }
                        }
                        if (!finished) {
                            if (request == null) {
                                Thread.yield();
                                Thread.sleep(50);
                            } else {
                                this.processRequest(request);
                            }

                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                Thread.yield();
            }
        }
    }
}
