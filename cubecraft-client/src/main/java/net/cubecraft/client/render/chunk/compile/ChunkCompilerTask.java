package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.render.chunk.TerrainRenderer;
import net.cubecraft.world.dump.ChunkCompileRegion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public abstract class ChunkCompilerTask extends Thread {
    public static final Logger LOGGER = LogManager.getLogger("ChunkCompilerTask");
    protected final TerrainRenderer owner;
    protected final Queue<ChunkCompileResult> resultQueue;
    protected final BlockingQueue<ChunkCompileRequest> requestQueue;
    protected final ModernChunkCompiler compiler;
    private final ChunkCompileRegion regionCache = new ChunkCompileRegion();
    private boolean running = true;

    public ChunkCompilerTask(TerrainRenderer owner) {
        this.owner = owner;
        this.requestQueue = owner.getRequestQueue();
        this.resultQueue = owner.getResultQueue();
        this.compiler = owner.getCompiler();
    }


    public static ChunkCompilerTask service(TerrainRenderer owner) {
        return new CompilerDaemon(owner);
    }

    public static ChunkCompilerTask task(TerrainRenderer owner, ChunkCompileRequest request) {
        return new CompilerTask(owner, request);
    }


    public void processRequest(ChunkCompileRequest request) {
        if (this.owner.isChunkOutOfRange(request.getPos())) {
            return;
        }

        this.compiler.build(this.regionCache, this.resultQueue, request.getWorld(), request);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    private static class CompilerTask extends ChunkCompilerTask {
        private final ChunkCompileRequest request;

        public CompilerTask(TerrainRenderer owner, ChunkCompileRequest request) {
            super(owner);
            this.request = request;
        }


        @Override
        public void run() {
            if (this.request == null) {
                return;
            }
            if (this.owner.isChunkOutOfRange(this.request.getPos())) {
                return;
            }
            this.processRequest(this.request);
        }
    }

    private static class CompilerDaemon extends ChunkCompilerTask {
        public CompilerDaemon(TerrainRenderer owner) {
            super(owner);
        }

        @Override
        public void run() {
            while (this.isRunning()) {
                try {
                    ChunkCompileRequest request;
                    synchronized ("chunk_poll") {
                        request = this.requestQueue.take();
                    }

                    if (this.owner.isChunkOutOfRange(request.getPos())) {
                        Thread.yield();
                        continue;
                    }

                    this.processRequest(request);
                } catch (InterruptedException ignored) {
                    return;
                } catch (Exception e) {
                    LOGGER.throwing(e);
                }
                Thread.yield();
            }
        }
    }
}
