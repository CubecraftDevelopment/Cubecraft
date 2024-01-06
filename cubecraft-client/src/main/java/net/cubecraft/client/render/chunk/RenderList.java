package net.cubecraft.client.render.chunk;

import net.cubecraft.client.render.LevelRenderContext;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.WorldRenderObject;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.Function;

public final class RenderList {
    private final Map<String, ChunkLayer> callList = new HashMap<>();
    private final Map<String, ChunkLayer> visibleCallList = new HashMap<>();
    private final List<RenderChunkPos> orderList = new ArrayList<>();
    private final List<String> types = new ArrayList<>();
    private int successDrawCount = 0;

    public RenderList(RenderType type) {
        for (ChunkLayer layer : ChunkRenderer.DUMMY.values()) {
            if (layer.getRenderType() == type) {
                this.types.add(layer.getID());
            }
        }
    }

    public int size() {
        return this.callList.size();
    }

    public void sort(Comparator<RenderChunkPos> sorter) {
        try {
            this.orderList.sort(sorter);
        } catch (IllegalArgumentException ignored) {
            //Comparison method violates its general contract,ignorable exception.
        }
    }

    public void putLayer(ChunkLayer layer) {
        if (layer == null) {
            return;
        }
        this.callList.put(layer.encode(), layer);
        if (this.orderList.contains(layer.getPos())) {
            return;
        }
        this.orderList.add(layer.getPos());
    }

    public void removeLayer(ChunkLayer layer) {
        this.callList.remove(layer.encode());
        for (String s : this.types) {
            if (this.callList.containsKey(ChunkLayer.encode(s, layer.getPos()))) {
                return;
            }
        }
        this.orderList.remove(layer.getPos());
    }

    public void removeLayer(String type, RenderChunkPos pos) {
        this.callList.remove(ChunkLayer.encode(type, pos));
        for (String s : this.types) {
            if (this.callList.containsKey(ChunkLayer.encode(s, pos))) {
                return;
            }
        }
        this.orderList.remove(pos);
    }

    public void removeAt(RenderChunkPos pos) {
        for (String s : this.types) {
            this.callList.remove(ChunkLayer.encode(s, pos));
        }
        this.orderList.remove(pos);
    }

    public void remove(Function<RenderChunkPos, Boolean> function) {
        for (RenderChunkPos pos : new ArrayList<>(this.orderList)) {
            if (!function.apply(pos)) {
                continue;
            }
            this.removeAt(pos);
        }
    }

    public void setVisibilityAt(RenderChunkPos pos, Boolean b) {
        for (String s : this.types) {
            String k = ChunkLayer.encode(s, pos);
            if (b) {
                this.visibleCallList.put(k, this.callList.get(k));
            } else {
                this.visibleCallList.remove(k, this.callList.get(k));
            }
        }
    }

    public void updateVisibility(Function<RenderChunkPos, Boolean> function) {
        for (RenderChunkPos pos : this.orderList) {
            this.setVisibilityAt(pos, function.apply(pos));
        }
    }

    public int drawAt(RenderChunkPos pos, Vector3d viewBase) {
        int count = 0;

        for (String s : this.types) {
            String k = ChunkLayer.encode(s, pos);
            ChunkLayer layer = this.visibleCallList.get(k);
            if (layer == null) {
                continue;
            }

            Vector3d translation = pos.getBaseWorldPosition().add(-viewBase.x, -viewBase.y, -viewBase.z);

            GL11.glPushMatrix();
            GL11.glTranslated(translation.x, translation.y, translation.z);
            layer.render();
            count++;
            GL11.glPopMatrix();
        }
        return count;
    }

    public void draw(Vector3d viewBase) {
        this.successDrawCount = 0;
        for (RenderChunkPos pos : this.orderList) {
            this.successDrawCount += this.drawAt(pos, viewBase);
        }
    }

    public int getSuccessDrawCount() {
        return this.successDrawCount;
    }

    public void clear() {
        this.callList.clear();
        this.orderList.clear();
        this.visibleCallList.clear();
        this.successDrawCount = 0;
    }

    public ChunkLayer getLayer(String k) {
        return this.callList.get(k);
    }

    public boolean containsLayer(String k) {
        return this.callList.containsKey(k);
    }

    public void sync(LevelRenderContext context) {
        List<WorldRenderObject> objList = new ArrayList<>(context.getObjects());
        List<ChunkLayer> visibleList=new ArrayList<>(this.visibleCallList.values());

        for (WorldRenderObject obj : objList) {
            if (!(obj instanceof ChunkLayer)) {
                continue;
            }
            if (!visibleList.contains(((ChunkLayer) obj))) {
                context.removeObject(obj);
            }
        }

        for (ChunkLayer layer : visibleList) {
            if (context.hasObject(layer)) {
                continue;
            }
            context.addObject(layer);
        }
    }
}
