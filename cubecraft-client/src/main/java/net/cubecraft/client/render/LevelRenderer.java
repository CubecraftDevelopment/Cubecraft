package net.cubecraft.client.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.flybird.fcommon.ColorUtil;
import ink.flybird.fcommon.container.MultiMap;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.memory.BufferAllocator;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.quantum3d.ColorElement;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.KeyboardReleaseEvent;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
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

import java.nio.FloatBuffer;
import java.util.Objects;

public final class LevelRenderer {
    public static final BufferAllocator ALLOCATOR = new LWJGLBufferAllocator(16384, 16777216);

    public final MultiMap<String, IWorldRenderer> renderers = new MultiMap<>();
    public final IWorld world;
    public final EntityPlayer player;
    public final Camera camera = new Camera();
    private FloatBuffer fogColor;

    public LevelRenderer(IWorld w, EntityPlayer p, CubecraftClient client, ResourceLocation cfgLoc) {
        this.fogColor = ALLOCATOR.allocFloatBuffer(5);

        client.getDeviceEventBus().registerEventListener(this);
        this.world = w;
        this.player = p;
        this.renderers.clear();
        String str = ClientSharedContext.RESOURCE_MANAGER.getResource(cfgLoc).getAsText();
        this.initialize(JsonParser.parseString(str).getAsJsonObject());
        client.getClientEventBus().callEvent(new ClientRendererInitializeEvent(client, this));

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.init();
        }
    }

    public void setFog(double dist) {
        Vector3d vec = this.player.getCameraPosition().add(this.player.getPosition());
        if (Objects.equals(this.world.getBlockAccess((long) vec.x, (long) vec.y, (long) vec.z).getBlockID(), "cubecraft:calm_water")) {
            GLUtil.setupFog(10, ColorUtil.int1Float1ToFloat4(0x050533, 1));
        } else {
            //GLUtil.setupFog((int) (Math.sqrt(d) * d), ColorUtil.int1Float1ToFloat4(this.getConfig().fogColor(), 1));
        }
    }

    public void initialize(JsonObject config) {
        CubecraftClient client = CubecraftClient.CLIENT;

        ColorElement.parseFromString(config.get("fog_color").getAsString()).toFloatRGBA(this.fogColor);
        JsonObject renderers = config.get("renderers").getAsJsonObject();

        for (String id : renderers.keySet()) {
            IWorldRenderer renderer = ClientRenderContext.WORLD_RENDERER.create(id);
            renderer.initializeRenderer(this, client.getWindow(), client.getClientWorld(), client.getPlayer(), this.camera);
            renderer.config(renderers.get(id).getAsJsonObject());
            client.getClientEventBus().registerEventListener(renderer);
            client.getDeviceEventBus().registerEventListener(renderer);
            this.renderers.put(id, renderer);
        }
    }

    public void render(float delta) {
        int d = ClientSettingRegistry.getFixedViewDistance();
        Vector3d vec = this.player.getCameraPosition().add(this.player.getPosition());
        if (Objects.equals(this.world.getBlockAccess((long) vec.x, (long) vec.y, (long) vec.z).getBlockID(), "cubecraft:calm_water")) {
            GLUtil.setupFog(d, ColorUtil.int1Float1ToFloat4(0x050533, 1));
        } else {
            //GLUtil.setupFog((int) (Math.sqrt(d) * d), ColorUtil.int1Float1ToFloat4(this.getConfig().fogColor(), 1));
        }

        float[] col = new float[]{0, 0, 0, 0};
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

    public FloatBuffer getFogColor() {
        return this.fogColor;
    }

    public <T extends IWorldRenderer> T getRenderer(String s, Class<T> type) {
        return type.cast(this.renderers.get(s));
    }
}
