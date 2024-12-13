package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.LegacyCamera;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.World;
import org.lwjgl.opengl.GL11;


public abstract class IWorldRenderer {
    private final BufferAllocator memoryAllocator = createMemoryAllocator();
    private final VertexBuilderAllocator vertexBuilderAllocator = new VertexBuilderAllocator(this.memoryAllocator);

    protected LevelRenderer parent;
    protected EntityPlayer player;
    protected World world;
    protected LegacyCamera camera;
    protected Window window;

    public void initializeRenderer(LevelRenderer parent, Window window, World world, EntityPlayer player, LegacyCamera cam) {
        this.world = world;
        this.player = player;
        this.camera = cam;
        this.window = window;
        this.parent = parent;
    }

    public final String getID() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }

    public Window getWindow() {
        return window;
    }

    public LegacyCamera getCamera() {
        return camera;
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

    protected void applyViewBobbing(float delta) {
        if (!this.player.isOnGround()) {
            //return;
        }
        float f = (float) (this.player.getWalkedDistance() - this.player.getLastWalkedDistance());
        float f1 = (float) -(this.player.getLastWalkedDistance() + f * delta);
        float f2 = 0.07f;//horizontal
        float f3 = 0.18f;//vertical
        GL11.glTranslated(Math.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(Math.cos(f1 * (float) Math.PI) * f2), 0.0F);
        GL11.glRotatef((float) (Math.sin(f1 * (float) Math.PI) * f2 * 3.0F), 0.0F, 0.0F, 1.0F);
        GL11.glRotatef((float) (Math.abs(Math.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
    }

    protected void setGlobalCamera(float delta) {
        GLUtil.setupPerspectiveCamera((float) Math.toRadians(this.camera.fov), this.window.getWidth(), this.window.getHeight());
        //GL11.glLoadIdentity();
        //GL11.glOrtho(0,window.getWindowWidth()*16f,0,window.getWindowHeight()*16f,,-1000000,1000000);
        //GL11.glLoadIdentity();
        //GL11.glTranslated(window.getWindowWidth()/4f,window.getWindowHeight()/4f,0);
        //GL11.glScaled(10,10,10);

        this.applyViewBobbing(delta);

        GL11.glTranslated(this.camera.getRelativePosition().x, this.camera.getRelativePosition().y, this.camera.getRelativePosition().z);
        GL11.glRotated(this.camera.getRotation().x, 1, 0, 0);
        GL11.glRotated(this.camera.getRotation().y, 0, 1, 0);
        GL11.glRotated(this.camera.getRotation().z, 0, 0, 1);
    }


    public VertexBuilderAllocator getVertexBuilderAllocator() {
        return vertexBuilderAllocator;
    }

    public BufferAllocator getMemoryAllocator() {
        return memoryAllocator;
    }
}