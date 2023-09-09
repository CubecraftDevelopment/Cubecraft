package ink.flybird.cubecraft.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.ClientRendererInitializeEvent;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.quantum3d.device.event.KeyboardReleaseEvent;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.world.IWorld;
import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.fcommon.ColorUtil;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.container.MultiMap;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.fcommon.math.MathHelper;
import org.lwjgl.opengl.GL11;

public final class LevelRenderer {
    public final MultiMap<String, IWorldRenderer> renderers = new MultiMap<>();
    public final IWorld world;
    public final EntityPlayer player;
    public final Camera camera = new Camera();
    private final GlobalRendererConfig config;

    public LevelRenderer(IWorld w, EntityPlayer p, CubecraftClient client, ResourceLocation cfgLoc) {
        client.getDeviceEventBus().registerEventListener(this);
        this.world = w;
        this.player = p;
        this.renderers.clear();
        String str = ClientRenderContext.RESOURCE_MANAGER.getResource(cfgLoc).getAsText();
        JsonArray obj = JsonParser.parseString(str).getAsJsonObject().get("renderers").getAsJsonArray();
        JsonObject global = JsonParser.parseString(str).getAsJsonObject().get("global").getAsJsonObject();
        this.config = GlobalRendererConfig.from(global);

        for (JsonElement element : obj) {
            String type = element.getAsJsonObject().get("type").getAsString();
            IWorldRenderer renderer = ClientRenderContext.WORLD_RENDERER.create(type, client.getWindow(), client.getClientWorld(), client.getPlayer(), camera, client.getGameSetting());
            renderer.config(element.getAsJsonObject().get("config"));
            this.renderers.put(type, renderer);
            renderer.setParent(this);
            client.getClientEventBus().registerEventListener(renderer);
            client.getDeviceEventBus().registerEventListener(renderer);
        }
        client.getClientEventBus().callEvent(new ClientRendererInitializeEvent(client, this));

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.refresh();
        }
    }

    public void setRenderState(GameSetting setting) {
        int d = setting.getValueAsInt("client.render.terrain.render_distance", 4);
        if (setting.getValueAsBoolean("client.render.fog", true)) {
            GL11.glEnable(GL11.GL_FOG);
            GLUtil.setupFog(d * d, ColorUtil.int1Float1ToFloat4(this.getConfig().fogColor(), 1));
        }else{
            GL11.glDisable(GL11.GL_FOG);
        }
    }

    public void closeState(GameSetting setting) {
        if (setting.getValueAsBoolean("client.render.fog", true)) {
            GL11.glDisable(GL11.GL_FOG);
        }
    }

    public void render(float interpolationTime) {
        float[] col = ColorUtil.int1Float1ToFloat4(this.getConfig().clearColor(), 1.0f);
        GL11.glClearColor(col[0], col[1], col[2], col[3]);
        //update camera position
        this.camera.setPos(
                MathHelper.linearInterpolate(this.player.xo, this.player.x, interpolationTime) + this.player.getCameraPosition().x,
                MathHelper.linearInterpolate(this.player.yo, this.player.y, interpolationTime) + this.player.getCameraPosition().y,
                MathHelper.linearInterpolate(this.player.zo, this.player.z, interpolationTime) + this.player.getCameraPosition().z);
        this.camera.setupRotation(this.player.xRot, this.player.yRot, this.player.zRot);
        this.camera.setPosRelative(0, 0, 0.15);

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.preRender();
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        render(RenderType.ALPHA, interpolationTime);
        GL11.glDepthMask(false);
        render(RenderType.TRANSPARENT, interpolationTime);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.postRender();
        }

        if (this.camera.isRotationChanged()) {
            this.camera.updateRotation();
        }
    }

    public void render(RenderType type, float delta) {
        if(type==RenderType.TRANSPARENT){
            GLUtil.enableBlend();
        }
        for (IWorldRenderer renderer : this.renderers.values()) {
            GL11.glPushMatrix();
            String rendererName = this.renderers.of(renderer);
            String postFix = "_%s|%s".formatted(rendererName, type.getName());

            renderer.preRender(type, delta);
            GLUtil.checkError("pre_render" + postFix);
            renderer.render(type, delta);
            GLUtil.checkError("shortTick" + postFix);
            renderer.postRender(type, delta);
            GLUtil.checkError("pre_render" + postFix);
            GL11.glPopMatrix();
        }
        if(type==RenderType.TRANSPARENT){
            GLUtil.disableBlend();
        }
    }

    @EventHandler
    public void onKey(KeyboardPressEvent event) {
        if (event.getKey() == KeyboardButton.KEY_F8) {
            this.refresh();
        }
        if (event.getKey() == KeyboardButton.KEY_C) {
            this.camera.setFov(20.0f);
        }
    }

    @EventHandler
    public void checkFov(KeyboardReleaseEvent event){
        if (event.getKey() == KeyboardButton.KEY_C) {
            this.camera.setFov(70.0f);
        }
    }

    public void stop(){
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.stop();
        }
    }

    public void refresh(){
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.refresh();
        }
    }

    public GlobalRendererConfig getConfig() {
        return config;
    }
}
