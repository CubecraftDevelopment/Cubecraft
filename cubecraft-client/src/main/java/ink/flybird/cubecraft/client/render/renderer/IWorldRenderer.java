package ink.flybird.cubecraft.client.render.renderer;

import com.google.gson.JsonElement;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.world.IWorld;
import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.fcommon.GameSetting;


public abstract class IWorldRenderer {
    protected final EntityPlayer player;
    protected final IWorld world;
    protected final Camera camera;
    protected final GameSetting setting;
    protected final Window window;
    protected LevelRenderer parent;


    public IWorldRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        this.world = world;
        this.player = player;
        this.camera = cam;
        this.setting = setting;
        this.window = window;
    }

    public final String getID() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }

    public void setParent(LevelRenderer parent) {
        this.parent = parent;
    }

    public GameSetting getSetting() {
        return setting;
    }

    public Window getWindow() {
        return window;
    }

    public Camera getCamera() {
        return camera;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public LevelRenderer getParent() {
        return parent;
    }


    //lifetime
    public void init(){}

    @Deprecated
    public void render(float interpolationTime){}

    public void stop(){
    }

    public void refresh() {
    }

    public void config(JsonElement json) {
    }


    //render
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