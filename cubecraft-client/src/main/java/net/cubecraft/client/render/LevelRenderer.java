package net.cubecraft.client.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gb2022.commons.ColorUtil;
import me.gb2022.commons.container.MultiMap;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.quantum3d.ColorElement;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.KeyboardReleaseEvent;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.LegacyCamera;
import me.gb2022.quantum3d.util.camera.Camera;
import me.gb2022.quantum3d.util.camera.PerspectiveCamera;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.ClientRendererInitializeEvent;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.World;
import net.cubecraft.world.WorldContext;
import net.cubecraft.world.block.blocks.Blocks;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class LevelRenderer extends ClientComponent {
    public static final BufferAllocator ALLOCATOR = new LWJGLBufferAllocator(16384, 16777216);
    public final MultiMap<String, IWorldRenderer> renderers = new MultiMap<>();

    public final Camera viewCamera = new PerspectiveCamera(70.0f);

    public final LegacyCamera camera = new LegacyCamera();
    private final FloatBuffer fogColor = ALLOCATOR.allocFloatBuffer(5);

    public World world;
    public EntityPlayer player;

    private ColorElement fogColorElement;
    private boolean active;


    public LevelRenderer() {
        this.camera.setPosRelative(0, 0, 0.15);
    }

    @Override
    public void worldContextChange(WorldContext context) {
        if (context == null) {
            this.stop();
            return;
        }
        var world = context.getWorld();
        var player = context.getPlayer();

        this.world = world;
        this.player = player;

        if (this.active) {
            this.stop();
        }
        this.init(ResourceLocation.worldRendererSetting(world.getId() + ".json"));
    }

    @Override
    public void clientSetup(CubecraftClient client) {
        super.clientSetup(client);

        client.getClientEventBus().registerEventListener(this);
        client.getDeviceEventBus().registerEventListener(this);
    }

    @Override
    public void tick() {
        if (!this.active) {
            return;
        }
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.tick();
        }
    }

    @Override
    public void render(DisplayScreenInfo info, float delta) {
        if (!this.active) {
            return;
        }

        var cameraPos = this.player.getCameraPosition();

        var x = MathHelper.linearInterpolate(this.player.xo, this.player.x, delta) + cameraPos.x;
        var y = MathHelper.linearInterpolate(this.player.yo, this.player.y, delta) + cameraPos.y;
        var z = MathHelper.linearInterpolate(this.player.zo, this.player.z, delta) + cameraPos.z;

        this.viewCamera.setPosition(x, y, z);
        this.viewCamera.updateMatrix();

        this.camera.setPos(x, y, z);
        this.camera.setupRotation(this.player.xRot, this.player.yRot, this.player.zRot);
        this.camera.setAspect(info.getAspect());

        for (IWorldRenderer renderer : this.renderers.values()) {
            GLUtil.checkError(renderer.getID() + ":pre:before");
            renderer.preRender();
            GLUtil.checkError(renderer.getID() + ":pre:after");
        }


        GLUtil.disableBlend();
        GLUtil.enableAlphaTest();
        render(RenderType.ALPHA, delta);

        GL11.glDepthMask(false);
        GLUtil.enableBlend();
        render(RenderType.TRANSPARENT, delta);
        GL11.glDepthMask(true);

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.postRender();
        }

        if (this.camera.isRotationChanged()) {
            this.camera.updateRotation();
        }
    }


    public void init(ResourceLocation configuration) {
        this.active = true;
        var dom = ClientSharedContext.RESOURCE_MANAGER.getResource(configuration).getAsText();
        var config = JsonParser.parseString(dom).getAsJsonObject();
        var eventBus = this.client.getClientEventBus();


        this.renderers.clear();

        this.fogColor.clear();
        this.fogColorElement = ColorElement.parseFromString(config.get("fog_color").getAsString());
        this.fogColorElement.toFloatRGBA(this.fogColor);

        JsonObject renderers = config.get("renderers").getAsJsonObject();

        for (String id : renderers.keySet()) {
            IWorldRenderer renderer = ClientRenderContext.WORLD_RENDERER.create(id);

            renderer.config(renderers.get(id).getAsJsonObject());
            client.getClientEventBus().registerEventListener(renderer);
            client.getDeviceEventBus().registerEventListener(renderer);
            this.renderers.put(id, renderer);
        }

        for (var renderer : this.renderers.values()) {
            renderer.initializeRenderer(this, client.getWindow(), this.world, this.player, this.camera);
        }

        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.init();
        }

        eventBus.callEvent(new ClientRendererInitializeEvent(this.client, this));
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

    public void stop() {
        for (IWorldRenderer renderer : this.renderers.values()) {
            renderer.stop();
        }
        this.active = false;
    }


    public void setFog(double dist) {
        var position = this.player.getCameraPosition().add(this.player.getPosition());
        var block = this.world.getBlockId((long) position.x, (long) position.y, (long) position.z);

        if (block == Blocks.WATER.getId()) {
            GLUtil.setupFog(10, ColorUtil.int1Float1ToFloat4(0x050533, 1));
            return;
        }

        GLUtil.setupFog((int) (Math.sqrt(dist) * 16), this.fogColorElement.RGBA_F());
    }

    public boolean isInBlock() {
        var position = this.player.getCameraPosition().add(this.player.getPosition());

        var x = Math.floor(position.x);
        var z = Math.floor(position.z);

        var block = this.world.getBlockId((long) x, (long) (position.y + 0.1f), (long) z);

        return block != Blocks.AIR.getId();
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


    public void refresh() {
        if (!this.active) {
            return;
        }

        this.stop();
        this.init(ResourceLocation.worldRendererSetting(this.world.getId() + ".json"));
    }


    public FloatBuffer getFogColor() {
        return this.fogColor;
    }

    public <T extends IWorldRenderer> T getRenderer(String s, Class<T> type) {
        return type.cast(this.renderers.get(s));
    }
}
