package net.cubecraft.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.flybird.fcommon.ColorUtil;
import ink.flybird.fcommon.container.MultiMap;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.KeyboardReleaseEvent;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.ClientRendererInitializeEvent;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.IWorld;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public final class LevelRenderer {
    public static final double CHUNK_DISTANCE_FIX_VALUE = 2;

    public final MultiMap<String, IWorldRenderer> renderers = new MultiMap<>();
    public final IWorld world;
    public final EntityPlayer player;
    public final Camera camera = new Camera();
    private final GlobalRendererConfig config;
    private final LevelRenderContext alphaRenderContext = new LevelRenderContext(RenderType.ALPHA, camera);
    private final LevelRenderContext transparentRenderContext = new LevelRenderContext(RenderType.TRANSPARENT, camera);

    public LevelRenderer(IWorld w, EntityPlayer p, CubecraftClient client, ResourceLocation cfgLoc) {
        client.getDeviceEventBus().registerEventListener(this);
        this.world = w;
        this.player = p;
        this.renderers.clear();
        String str = ClientSharedContext.RESOURCE_MANAGER.getResource(cfgLoc).getAsText();
        JsonArray obj = JsonParser.parseString(str).getAsJsonObject().get("renderers").getAsJsonArray();
        JsonObject global = JsonParser.parseString(str).getAsJsonObject().get("global").getAsJsonObject();
        this.config = GlobalRendererConfig.from(global);

        for (JsonElement element : obj) {
            String type = element.getAsJsonObject().get("type").getAsString();
            IWorldRenderer renderer = ClientRenderContext.WORLD_RENDERER.create(type, client.getWindow(), client.getClientWorld(), client.getPlayer(), camera);
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

    public void render(float delta) {
        //new
        GL11.glEnable(GL11.GL_CULL_FACE);
        GLUtil.enableAlphaTest();
        this.renderContext(this.alphaRenderContext);
        GL11.glDepthMask(false);
        GLUtil.enableBlend();
        this.renderContext(this.transparentRenderContext);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);

        //deprecated
        GL11.glDisable(GL11.GL_FOG);
        int d = ClientSettingRegistry.getFixedViewDistance();
        Vector3d vec = this.player.getCameraPosition().add(this.player.getPosition());
        if (Objects.equals(this.world.getBlockAccess((long) vec.x, (long) vec.y, (long) vec.z).getBlockID(), "cubecraft:calm_water")) {
            GLUtil.setupFog(d, ColorUtil.int1Float1ToFloat4(0x050533, 1));
        } else {
            //GLUtil.setupFog(d * d, ColorUtil.int1Float1ToFloat4(this.getConfig().fogColor(), 1));
        }

        float[] col = ColorUtil.int1Float1ToFloat4(this.getConfig().clearColor(), 1.0f);
        GL11.glClearColor(col[0], col[1], col[2], col[3]);
        this.camera.setPos(
                MathHelper.linearInterpolate(this.player.xo, this.player.x, delta) + this.player.getCameraPosition().x,
                MathHelper.linearInterpolate(this.player.yo, this.player.y, delta) + this.player.getCameraPosition().y,
                MathHelper.linearInterpolate(this.player.zo, this.player.z, delta) + this.player.getCameraPosition().z);
        this.camera.setupRotation(this.player.xRot, this.player.yRot, this.player.zRot);
        this.camera.setPosRelative(0, 0, 0.15);

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.preRender();
        }

        //GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GLUtil.disableBlend();
        GLUtil.enableAlphaTest();
        render(RenderType.ALPHA, delta);

        GL11.glDepthMask(false);
        GLUtil.enableBlend();
        render(RenderType.TRANSPARENT, delta);
        GL11.glDepthMask(true);

        GL11.glDisable(GL11.GL_CULL_FACE);

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.postRender();
        }

        if (this.camera.isRotationChanged()) {
            this.camera.updateRotation();
        }
    }


    public void renderContext(LevelRenderContext context) {
        this.camera.setUpGlobalCamera();
        for (IWorldRenderer renderer : this.renderers.values()) {
            //renderer.preRender(context);
        }
        GLUtil.checkError("pre_render");
        GLUtil.enableClientState();
        context.renderObjects();
        GLUtil.disableClientState();
        GLUtil.checkError("render_context");
        for (IWorldRenderer renderer : this.renderers.values()) {
            //renderer.postRender(context);
        }
        GLUtil.checkError("post_render");
    }


    public void render(RenderType type, float delta) {
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
    public void checkFov(KeyboardReleaseEvent event) {
        if (event.getKey() == KeyboardButton.KEY_C) {
            this.camera.setFov(70.0f);
        }
    }

    public void stop() {
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.stop();
        }
    }

    public void refresh() {
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.refresh();
        }
    }

    public void tick() {
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.tick();
        }
    }

    public GlobalRendererConfig getConfig() {
        return config;
    }

    public interface SettingHolder {

    }
}
