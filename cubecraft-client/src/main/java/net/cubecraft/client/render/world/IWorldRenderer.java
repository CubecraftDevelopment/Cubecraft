package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import me.gb2022.quantum3d.util.camera.Camera;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.World;


public abstract class IWorldRenderer {
    private final BufferAllocator memoryAllocator = createMemoryAllocator();
    private final VertexBuilderAllocator vertexBuilderAllocator = new VertexBuilderAllocator(this.memoryAllocator);

    protected LevelRenderer parent;
    protected EntityPlayer player;
    protected World world;
    protected Camera<?> viewCamera;
    protected Window window;

    public void initializeRenderer(LevelRenderer parent, Window window, World world, EntityPlayer player, Camera<?> viewCamera) {
        this.world = world;
        this.player = player;
        this.window = window;
        this.viewCamera = viewCamera;
        this.parent = parent;
    }

    public final String getID() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }

    public Window getWindow() {
        return window;
    }

    public BufferAllocator createMemoryAllocator() {
        return new LWJGLBufferAllocator(4096, 33554432 * 8);
    }

    public void init() {
    }

    public void stop() {
    }

    public void tick() {
    }

    public void preRender() {
    }

    public void preRender(RenderType type, float delta) {
    }

    public abstract void render(RenderType type, float delta);

    public void postRender(RenderType type, float delta) {
    }

    public void postRender() {
    }

    public void config(JsonObject json) {
    }


    public VertexBuilderAllocator getVertexBuilderAllocator() {
        return vertexBuilderAllocator;
    }

    public BufferAllocator getMemoryAllocator() {
        return memoryAllocator;
    }

    public Camera<?> getViewCamera() {
        return this.viewCamera;
    }
}