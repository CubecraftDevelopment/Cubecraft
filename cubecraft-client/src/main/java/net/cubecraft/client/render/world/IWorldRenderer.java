package net.cubecraft.client.render.world;

import com.google.gson.JsonElement;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;

import me.gb2022.quantum3d.device.Window;


public abstract class IWorldRenderer {
    protected final EntityPlayer player;
    protected final IWorld world;
    protected final Camera camera;
    protected final Window window;
    protected LevelRenderer parent;


    public IWorldRenderer(Window window, IWorld world, EntityPlayer player, Camera cam) {
        this.world = world;
        this.player = player;
        this.camera = cam;
        this.window = window;
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

    public void setParent(LevelRenderer parent) {
        this.parent = parent;
    }

    public void refresh() {
        this.stop();
        this.init();
    }


    public void config(JsonElement json) {
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

    public void render(RenderType type, float delta) {
    }

    public void postRender(RenderType type, float delta) {
    }

    public void postRender() {
    }
}