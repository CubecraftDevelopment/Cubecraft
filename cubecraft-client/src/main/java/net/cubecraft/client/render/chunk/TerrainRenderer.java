package net.cubecraft.client.render.chunk;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.memory.BlockedBufferAllocator;
import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.memory.LWJGLBlockedBufferAllocator;
import me.gb2022.quantum3d.memory.LWJGLSecureMemoryManager;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.camera.ViewFrustum;
import net.cubecraft.client.ClientContext;
import net.cubecraft.client.registry.ClientSettings.RenderSetting.WorldSetting.ChunkSetting;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.chunk.compile.ChunkCompileRequest;
import net.cubecraft.client.render.chunk.compile.ChunkCompileResult;
import net.cubecraft.client.render.chunk.compile.ChunkCompilerTask;
import net.cubecraft.client.render.chunk.compile.ModernChunkCompiler;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainer;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.client.render.chunk.sort.ChunkCompileRequestSorter;
import net.cubecraft.client.render.chunk.sort.ChunkCompileResultSorter;
import net.cubecraft.client.render.chunk.sort.ChunkSorter;
import net.cubecraft.client.render.chunk.status.*;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3i;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@TypeItem("cubecraft:chunk_renderer")
public final class TerrainRenderer extends IWorldRenderer implements ChunkStatusHandler {
    public static final Logger LOGGER = LogManager.getLogger("TerrainRenderer");

    private final int viewDistance = ChunkSetting.getFixedViewDistance();

    private final ChunkMarkCache chunkStatusCache = new ModernChunkStatusCache(this);
    private final ChunkStatusHandlerThread daemon = ChunkStatusHandlerThread.create(this);
    private final ModernChunkCompiler compiler = new ModernChunkCompiler(this);
    private final ViewFrustum frustum = new ViewFrustum();

    private final Int2ObjectMap<ChunkLayerContainer> alphaContainers = new Int2ObjectOpenHashMap<>(8);
    private final Int2ObjectMap<ChunkLayerContainer> transparentContainers = new Int2ObjectOpenHashMap<>(8);
    private final Int2ObjectMap<ChunkLayerContainer> containers = new Int2ObjectOpenHashMap<>(8);

    private final ChunkCompileRequestSorter chunkCompileRequestSorter = new ChunkCompileRequestSorter(this.frustum);
    private final BlockingQueue<ChunkCompileRequest> requestQueue = new PriorityBlockingQueue<>(4096, this.chunkCompileRequestSorter);
    private final ChunkCompileResultSorter chunkCompileResultSorter = new ChunkCompileResultSorter(this.frustum);
    private final Queue<ChunkCompileResult> resultQueue = new PriorityQueue<>(this.chunkCompileResultSorter);

    private final ChunkSorter chunkSorter;
    private final Vector3i lastChunkPos = new Vector3i();

    private ChunkCompilerTask[] compilers;

    public TerrainRenderer() {
        this.chunkSorter = new ChunkSorter();

        for (var provider : ChunkLayerContainers.REGISTRY.entries()) {
            var f = provider.get();

            var map = switch (f.getRenderType()) {
                case VISIBLE_AREA -> throw new IllegalArgumentException("VISIBLE_AREA should have no render-containers assigned");
                case ALPHA -> this.alphaContainers;
                case TRANSPARENT -> this.transparentContainers;
            };

            var id = provider.getId();
            boolean vbo = ChunkSetting.VBO.getValue();
            var layer = f.getFactory().createChunkLayer(this, this.viewDistance, vbo);

            map.put(id, layer);
            this.containers.put(id, layer);
        }

        LOGGER.info("created {} alpha renderers, {} transparent renderers", this.alphaContainers.size(), this.transparentContainers.size());

        ClientContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "D/A" -> this.alphaContainers.values().stream().mapToInt((c) -> c.getLayers().size()).sum();
            case "D/T" -> this.transparentContainers.values().stream().mapToInt((c) -> c.getLayers().size()).sum();
            case "D/A_S" -> this.alphaContainers.values().stream().mapToInt((c) -> c.getVisibleLayers().size()).sum();
            case "D/T_S" -> this.transparentContainers.values().stream().mapToInt((c) -> c.getVisibleLayers().size()).sum();

            case "C/D" -> this.resultQueue.size();
            case "C/R" -> this.requestQueue.size();
            case "SC" -> this.chunkStatusCache.toString();
            default -> 0;
        }));
    }

    public static double chunkDistance(double vx, double vy, double vz, int cx, int cy, int cz) {
        var ccx = (((long) Math.floor(vx))) >> 4;
        var ccy = (((long) Math.floor(vy))) >> 4;
        var ccz = (((long) Math.floor(vz))) >> 4;

        var dx = cx - ccx;
        var dy = cy - ccy;
        var dz = cz - ccz;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public BufferAllocator createMemoryAllocator() {
        var bs = 36 * 8192;
        var bc = 512;

        return new LWJGLBlockedBufferAllocator(LWJGLSecureMemoryManager.allocate(bs * bc), bs, bc);
    }

    @SuppressWarnings("InstantiatingAThreadWithDefaultRunMethod")
    @Override
    public void init() {
        this.world.getEventBus().registerEventListener(this);
        int count = ChunkSetting.UPDATE_THREAD.getValue();
        this.compilers = new ChunkCompilerTask[count];
        for (int i = 0; i < count; i++) {
            ChunkCompilerTask task = ChunkCompilerTask.service(this);
            this.compilers[i] = task;
            task.setName("%s#%d-%d".formatted("ChunkCompiler", hashCode(), i));
            task.setDaemon(true);
            task.setPriority(1);
            task.start();
        }

        this.daemon.start();
    }

    @Override
    public void tick() {
        var x = this.viewCamera.getX();
        var y = this.viewCamera.getY();
        var z = this.viewCamera.getZ();

        this.chunkSorter.setPos(x, y, z);
        this.chunkCompileRequestSorter.setPos(x, y, z);
        this.chunkCompileResultSorter.setPos(x, y, z);

        for (var container : this.containers.values()) {
            container.remove((l) -> isChunkOutOfRange(l.getOwner().getX(), l.getOwner().getY(), l.getOwner().getZ(), 0));
            container.lazyUpdate();
        }
    }

    @Override
    public void preRender() {
        for (int i = 0; i < ChunkSetting.MAX_RECEIVE.getValue(); i++) {
            if (this.resultQueue.isEmpty()) {
                return;
            }
            var result = this.resultQueue.poll();
            if (result == null) {
                continue;
            }

            if (this.isChunkOutOfRange(result.getX(), result.getY(), result.getZ(), 2)) {
                result.free();
                i--;
                Thread.yield();
                continue;
            }


            for (var n = 0; n < result.getLayers().length; n++) {
                var layer = result.getLayers()[n];
                var container = this.containers.get(layer);

                var x = result.getX();
                var y = result.getY();
                var z = result.getZ();

                if (container == null) {
                    continue;
                }

                if (result.failed()) {
                    container.removeLayer(x, y, z);
                    continue;
                }

                if (result.isLayerComplete(n)) {
                    container.handle(result.getBuilders(n), x, y, z);
                } else {
                    container.removeLayer(x, y, z);
                }
            }

            result.free();
        }
    }

    public boolean isChunkOutOfRange(int x, int y, int z, int addition) {
        if (y < -1 || y > (Chunk.HEIGHT >> 4) + 1) {
            return true;
        }

        var vx = this.viewCamera.getX();
        var vy = this.viewCamera.getY();
        var vz = this.viewCamera.getZ();

        return chunkDistance(vx, vy, vz, x, y, z) > this.viewDistance + addition;
    }

    @Override
    public void preRender(RenderType type, float delta) {
        this.frustum.update(this.viewCamera);
        this.parent.setFog(this.viewDistance * 16);
    }

    @Override
    public void render(RenderType type, float deltas) {
        var vx = this.viewCamera.getX();
        var vy = this.viewCamera.getY();
        var vz = this.viewCamera.getZ();

        if (type == RenderType.ALPHA) {
            GLUtil.enableClientState();
            for (var container : this.alphaContainers.values()) {
                container.render(vx, vy, vz, this.window.getFrame());
            }
            GLUtil.disableClientState();
        } else {
            GLUtil.enableClientState();
            for (var container : this.transparentContainers.values()) {
                container.render(vx, vy, vz, this.window.getFrame());
            }
            GLUtil.disableClientState();
        }

        var cx = ((long) Math.floor(vx)) >> 4;
        var cy = ((long) Math.floor(vy)) >> 4;
        var cz = ((long) Math.floor(vz)) >> 4;

        this.lastChunkPos.set((int) cx, (int) cy, (int) cz);
    }

    @Override
    public void stop() {
        this.daemon.setRunning(false);
        for (ChunkCompilerTask daemon : this.compilers) {
            daemon.setRunning(false);
            daemon.interrupt();
        }

        for (var container : this.containers.values()) {
            container.clear();
        }

        this.containers.clear();
        this.chunkStatusCache.delete();
        this.requestQueue.clear();
        this.resultQueue.clear();

        ClientContext.QUERY_HANDLER.unregisterCallback(this.getID());
        this.world.getEventBus().unregisterEventListener(this);

        this.getMemoryAllocator().clear();
        LWJGLSecureMemoryManager.free(((BlockedBufferAllocator) this.getMemoryAllocator()).getHandle());
    }

    public void setUpdate(int x, int y, int z, boolean immediate) {
        var request = ChunkCompileRequest.build(this.world, x, y, z, ChunkLayerContainers.REGISTRY.ids());

        if (immediate) {
            //direct
            //noinspection CallToThreadRun
            ChunkCompilerTask.task(this, request).run();
            return;
        }
        this.requestQueue.add(request);
    }

    public void update(int cx, int cy, int cz, int rx, int ry, int rz, boolean sync) {
        var xd = rx == 0;
        var xu = rx == 15;
        var yd = ry == 0;
        var yu = ry == 15;
        var zd = rz == 0;
        var zu = rz == 15;

        var xe = !xd && !xu;
        var ye = !yd && !yu;
        var ze = !zd && !zu;

        setUpdate(cx, cy, cz, sync);

        //face
        if (xd) {
            setUpdate(cx - 1, cy, cz, sync);
        }
        if (xu) {
            setUpdate(cx + 1, cy, cz, sync);
        }
        if (yd) {
            setUpdate(cx, cy - 1, cz, sync);
        }
        if (yu) {
            setUpdate(cx, cy, cz + 1, sync);
        }
        if (zd) {
            setUpdate(cx, cy, cz - 1, sync);
        }
        if (zu) {
            setUpdate(cx, cy, cz + 1, sync);
        }

        //corner
        if (xd && yd && zd) {
            setUpdate(cx - 1, cy - 1, cz - 1, sync);
        }
        if (xu && yd && zd) {
            setUpdate(cx + 1, cy - 1, cz - 1, sync);
        }
        if (xd && yu && zd) {
            setUpdate(cx - 1, cy + 1, cz - 1, sync);
        }
        if (xu && yu && zd) {
            setUpdate(cx + 1, cy + 1, cz - 1, sync);
        }
        if (xd && yd && zu) {
            setUpdate(cx - 1, cy - 1, cz + 1, sync);
        }
        if (xu && yd && zu) {
            setUpdate(cx + 1, cy - 1, cz + 1, sync);
        }
        if (xd && yu && zu) {
            setUpdate(cx - 1, cy + 1, cz + 1, sync);
        }
        if (xu && yu && zu) {
            setUpdate(cx + 1, cy + 1, cz + 1, sync);
        }

        //edge x-plane
        if (xe && yd && zd) {
            setUpdate(cx, cy - 1, cz - 1, sync);
        }
        if (xe && yu && zd) {
            setUpdate(cx, cy + 1, cz - 1, sync);
        }
        if (xe && yd && zu) {
            setUpdate(cx, cy - 1, cz + 1, sync);
        }
        if (xe && yu && zu) {
            setUpdate(cx, cy + 1, cz + 1, sync);
        }

        //edge y-plane
        if (xd && ye && zd) {
            setUpdate(cx - 1, cy, cz - 1, sync);
        }
        if (xu && ye && zd) {
            setUpdate(cx + 1, cy, cz - 1, sync);
        }
        if (xd && ye && zu) {
            setUpdate(cx - 1, cy, cz + 1, sync);
        }
        if (xu && ye && zu) {
            setUpdate(cx + 1, cy, cz + 1, sync);
        }

        //edge z-plane
        if (xd && yd && ze) {
            setUpdate(cx - 1, cy - 1, cz, sync);
        }
        if (xd && yu && ze) {
            setUpdate(cx - 1, cy + 1, cz, sync);
        }
        if (xu && yd && ze) {
            setUpdate(cx + 1, cy - 1, cz, sync);
        }
        if (xu && yu && ze) {
            setUpdate(cx + 1, cy + 1, cz, sync);
        }
    }

    @EventHandler
    public void blockChanged(BlockIDChangedEvent e) {
        long x = e.x(), y = e.y(), z = e.z();

        var cx = (int) (x >> 4);
        var cy = (int) (y >> 4);
        var cz = (int) (z >> 4);

        update(cx, cy, cz, (int) (x & 15), (int) (y & 15), (int) (z & 15), true);
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public ChunkMarkCache getStatusCache() {
        return this.chunkStatusCache;
    }

    public BlockingQueue<ChunkCompileRequest> getRequestQueue() {
        return this.requestQueue;
    }

    public Queue<ChunkCompileResult> getResultQueue() {
        return this.resultQueue;
    }

    public ModernChunkCompiler getCompiler() {
        return this.compiler;
    }

    @Override
    public void testFace(int x, int y, int z, boolean[] v) {
        var vx = this.viewCamera.getX();
        var vy = this.viewCamera.getY();
        var vz = this.viewCamera.getZ();

        var state = this.chunkStatusCache.get(x, y, z);

        if (state == null) {
            return;
        }


        var bounding = state.getBounding();

        v[0] = vy > bounding.y0;
        v[1] = vy < bounding.y1;
        v[2] = vz > bounding.z0;
        v[3] = vz < bounding.z1;
        v[4] = vx > bounding.x0;
        v[5] = vx < bounding.x1;
    }

    @Override
    public boolean testFrustum(int x, int y, int z) {
        var state = this.chunkStatusCache.get(x, y, z);

        if (state == null) {
            return true;
        }

        return this.frustum.boxVisible(state.getBounding(), this.viewCamera.getX(), this.viewCamera.getY(), this.viewCamera.getZ());
    }

    @Override
    public boolean apply(int x, int y, int z) {
        if (!this.frustum.boxVisible(RenderChunkPos.getBounding(
                this.viewCamera.getX(),
                this.viewCamera.getY(),
                this.viewCamera.getZ(),
                x,
                y,
                z
        ))) {
            return false;
        }
        this.setUpdate(x, y, z, false);
        return true;
    }

    public Vector3i getLastChunkPos() {
        return lastChunkPos;
    }
}