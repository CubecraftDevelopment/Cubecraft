package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;

import me.gb2022.quantum3d.device.Window;


public abstract class IWorldRenderer {
    protected LevelRenderer parent;
    protected EntityPlayer player;
    protected IWorld world;
    protected Camera camera;
    protected Window window;

    public void initializeRenderer(LevelRenderer parent,Window window, IWorld world, EntityPlayer player, Camera cam){
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

    public Camera getCamera() {
        return camera;
    }

    public final void refresh() {
        this.stop();
        this.init();
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

    public void config(JsonObject json){
    }
}