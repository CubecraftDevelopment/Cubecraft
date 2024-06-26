package net.cubecraft.client.render.chunk.compile;

import ink.flybird.quantum3d_legacy.BufferAllocation;
import net.cubecraft.client.render.chunk.ChunkRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public abstract class ChunkCompilerTask implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger("Client/ChunkCompilerTask");

    protected final Queue<ChunkCompileResult> resultQueue;
    protected final Queue<ChunkCompileRequest> requestQueue;
    protected final ChunkRenderer parent;

    private boolean running = true;

    protected ChunkCompilerTask(ChunkRenderer parent, Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
        this.parent = parent;
        this.resultQueue = result;
        this.requestQueue = request;
    }

    public static ChunkCompilerTask daemon(ChunkRenderer parent, Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
        return new CompilerDaemon(parent, request, result);
    }

    public static ChunkCompilerTask task(ChunkRenderer parent, ChunkCompileRequest request, Queue<ChunkCompileResult> result) {
        return new CompilerTask(parent, request, result);
    }

    public void processRequest(ChunkCompileRequest request) {
        IWorld world = request.getWorld();
        RenderChunkPos pos = request.getPos();
        String layerId = request.getLayerId();

        ChunkCompileResult result;
        if (this.parent.isChunkOutOfRange(request.getPos())) {
            return;
        }

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
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    private static class CompilerTask extends ChunkCompilerTask {
        private final ChunkCompileRequest request;

        protected CompilerTask(ChunkRenderer parent, ChunkCompileRequest request, Queue<ChunkCompileResult> result) {
            super(parent, null, result);
            this.request = request;
        }

        @Override
        public void run() {
            if (this.request == null) {
                return;
            }
            if (this.parent.isChunkOutOfRange(this.request.getPos())) {
                return;
            }
            this.processRequest(this.request);
        }
    }

    private static class CompilerDaemon extends ChunkCompilerTask {
        protected CompilerDaemon(ChunkRenderer parent, Queue<ChunkCompileRequest> request, Queue<ChunkCompileResult> result) {
            super(parent, request, result);
        }

        @Override
        public void run() {
            while (this.isRunning()) {
                try {
                    while (BufferAllocation.getAllocSize() > Integer.MAX_VALUE) {
                        Thread.onSpinWait();
                        Thread.sleep(50);
                        Thread.yield();
                    }
                    while (this.requestQueue.isEmpty()) {
                        Thread.onSpinWait();
                        Thread.sleep(50);
                        Thread.yield();
                    }

                    for (int i = 0; i < 40; i++) {
                        ChunkCompileRequest request;
                        if (this.requestQueue.isEmpty()) {
                            Thread.yield();
                            Thread.sleep(1);
                            continue;
                        } else {
                            synchronized ("chunk_poll") {
                                request = this.requestQueue.poll();
                            }
                        }

                        if (request == null || this.parent.isChunkOutOfRange(request.getPos())) {
                            Thread.yield();
                            continue;
                        }

                        this.processRequest(request);
                    }
                    Thread.sleep(5);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                Thread.yield();
            }
        }
    }
}
