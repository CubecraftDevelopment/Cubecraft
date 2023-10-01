package ink.flybird.cubecraft.client.render.chunk.compile;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.container.ArrayQueue;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.quantum3d_legacy.BufferAllocation;

public abstract class ChunkCompilerTask implements Runnable{
    private static final Logger LOGGER = SharedContext.LOG_CONTEXT.createLogger("chunk_compiler_task");
    protected final ArrayQueue<ChunkCompileResult> resultQueue;
    protected final ArrayQueue<ChunkCompileRequest> requestQueue;

    protected ChunkCompilerTask(ArrayQueue<ChunkCompileRequest> request, ArrayQueue<ChunkCompileResult> result) {
        this.resultQueue = result;
        this.requestQueue = request;
    }

    public static ChunkCompilerTask daemon(ArrayQueue<ChunkCompileRequest> request, ArrayQueue<ChunkCompileResult> result) {
        return new CompilerDaemon(request, result);
    }

    public static ChunkCompilerTask task(ArrayQueue<ChunkCompileRequest> request, ArrayQueue<ChunkCompileResult> result) {
        return new CompilerTask(request, result);
    }

    protected void process() {
        while (BufferAllocation.getAllocSize() > 67108864 * 8) {
            Thread.yield();
        }
        if (this.requestQueue.size() <= 0) {
            return;
        }

        try {
            ChunkCompileRequest request = this.requestQueue.poll();
            if(request==null){
                return;
            }
            IWorld world = request.getWorld();
            RenderChunkPos pos = request.getPos();
            String layerId = request.getLayerId();
            if (request.getLayer() == null) {
                this.resultQueue.add(ChunkCompiler.build(layerId, world, pos));
            } else {
                this.resultQueue.add(ChunkCompiler.rebuild(layerId, world, pos, request.getLayer()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setRunning(boolean b) {
    }

    private static class CompilerDaemon extends ChunkCompilerTask {
        private boolean running = true;

        protected CompilerDaemon(ArrayQueue<ChunkCompileRequest> requestQueue, ArrayQueue<ChunkCompileResult> resultQueue) {
            super(requestQueue, resultQueue);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.process();
                }catch (Exception e){
                    e.printStackTrace();
                    System.exit(0);
                }
                Thread.yield();
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }

    private static class CompilerTask extends ChunkCompilerTask {
        protected CompilerTask(ArrayQueue<ChunkCompileRequest> requestQueue, ArrayQueue<ChunkCompileResult> resultQueue) {
            super(requestQueue, resultQueue);
        }

        @Override
        public void run() {
            this.process();
        }
    }
}
