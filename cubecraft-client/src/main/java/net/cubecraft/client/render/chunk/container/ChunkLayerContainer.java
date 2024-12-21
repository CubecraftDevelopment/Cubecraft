package net.cubecraft.client.render.chunk.container;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.ChunkLayer;
import net.cubecraft.client.render.chunk.TerrainRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class ChunkLayerContainer {
    private final ChunkLayerContainerFactory.Provider provider;
    private final Long2ObjectMap<ChunkLayer> layers;
    private final List<ChunkLayer> visibleLayers = new ArrayList<>(1024);
    private final boolean useVBO;
    private final TerrainRenderer parent;

    protected ChunkLayerContainer(TerrainRenderer parent, int viewRange, boolean useVBO) {
        this.layers = new Long2ObjectOpenHashMap<>((int) (viewRange * viewRange * viewRange * 0.325));
        this.useVBO = useVBO;
        this.parent = parent;
        this.provider = ChunkLayerContainers.REGISTRY.object(getId());
    }

    public void lazyUpdate() {
        var it = this.visibleLayers.iterator();
        while (it.hasNext()) {
            var layer = it.next();
            if (!layer.getOwner().isFrustumVisible()) {
                it.remove();
                layer.setActive(false);
            }
        }
    }

    public int group(int v) {
        return (v + 32768) / 65535;
    }

    public void render(double vx, double vy, double vz, long frame) {
        var cx = ((long) Math.floor(vx)) >> 4;
        var cy = ((long) Math.floor(vy)) >> 4;
        var cz = ((long) Math.floor(vz)) >> 4;

        var camera = this.parent.getViewCamera();
        var lc = this.parent.getLastChunkPos();

        var chunkPosChanged = cx != lc.x() || cy != lc.y() || cz != lc.z();
        var camPosChanged = vx != camera.getLastX() || vy != camera.getLastY() || vz != camera.getLastZ();


        if (this.parent.getViewCamera().isRotationChanged() || camPosChanged) {
            for (var layer : this.layers.values()) {
                layer.getOwner().updateFrustumVisibility(frame, this.parent);
                if (this.updateLayerVisible(layer) && layer.getOwner().checkFaceVisibilityDirty((int) cx, (int) cz)) {
                    layer.getOwner().updateFaceVisibility(frame, this.parent);
                }
            }
        }

        if (chunkPosChanged) {
            for (var layer : this.visibleLayers) {
                layer.getOwner().updateFaceVisibility(frame, this.parent);
            }
        }


        if (this.visibleLayers.isEmpty()) {
            return;
        }

        this.setup();

        var matX = Integer.MIN_VALUE;
        var matZ = Integer.MIN_VALUE;

        camera.push();

        for (var layer : this.visibleLayers) {
            var x = layer.getOwner().getX() * 16;
            var z = layer.getOwner().getZ() * 16;

            var gx = group(x);
            var gz = group(z);

            if (gx != matX || gz != matZ) {

                camera.pop();

                var grx = gx * 65536;
                var grz = gz * 65536;

                camera.push().object(grx, 0, grz).set();

                matX = gx;
                matZ = gz;
            }

            layer.render();
        }

        camera.pop();
    }

    public boolean updateLayerVisible(ChunkLayer layer) {
        if (layer.isActive()) {
            return false;
        }
        if (!layer.getOwner().isFrustumVisible()) {
            return false;
        }
        addVisibleLayer(layer);
        return true;
    }


    public void addVisibleLayer(ChunkLayer layer) {
        this.visibleLayers.add(layer);
        layer.setActive(true);
    }


    public abstract void setup();

    public abstract int getId();

    public void setLayer(int x, int y, int z, ChunkLayer layer) {
        this.layers.put(ChunkLayer.hash(x, y, z), layer);
    }

    public void removeLayer(int x, int y, int z) {
        var layer = this.layers.remove(ChunkLayer.hash(x, y, z));
        this.visibleLayers.remove(layer);
        if (layer == null) {
            return;
        }
        layer.setActive(false);
    }

    public boolean hasLayer(int x, int y, int z) {
        return this.layers.containsKey(ChunkLayer.hash(x, y, z));
    }

    public ChunkLayer getLayer(int x, int y, int z) {
        return layers.get(ChunkLayer.hash(x, y, z));
    }

    public void handle(VertexBuilder[] builder, int x, int y, int z) {
        if (!hasLayer(x, y, z)) {
            var owner = this.parent.getStatusCache().getWithFallback(x, y, z);
            var layer = new ChunkLayer(owner, this.useVBO);
            layer.allocate();
            this.setLayer(x, y, z, layer);

            var frame = this.parent.getWindow().getFrame();

            layer.getOwner().updateFrustumVisibility(frame, this.parent);

            if (this.updateLayerVisible(layer)) {
                layer.getOwner().updateFaceVisibility(frame, this.parent);
            }
        }

        getLayer(x, y, z).upload(builder);
    }

    public void sortVisible(Comparator<ChunkLayer> layerSorter) {
        this.visibleLayers.sort(layerSorter);
    }

    public void remove(Predicate<ChunkLayer> filter) {
        var it = this.visibleLayers.iterator();
        while (it.hasNext()) {
            var layer = it.next();
            if (filter.test(layer)) {
                it.remove();
                layer.setActive(false);

                layer.destroy();
            }
        }

        for (var layer : this.layers.values().toArray(new ChunkLayer[0])) {
            if (!filter.test(layer)) {
                continue;
            }
            var a = this.layers.remove(ChunkLayer.hash(layer.getOwner().getX(), layer.getOwner().getY(), layer.getOwner().getZ()));

            if (a != null) {
                a.destroy();
            }
        }
    }

    public void clear() {
        this.visibleLayers.clear();
        this.layers.clear();
    }

    public List<ChunkLayer> getVisibleLayers() {
        return this.visibleLayers;
    }

    public Long2ObjectMap<ChunkLayer> getLayers() {
        return this.layers;
    }

    public TerrainRenderer getParent() {
        return parent;
    }

    public ChunkLayerContainerFactory.Provider getProvider() {
        return provider;
    }
}
