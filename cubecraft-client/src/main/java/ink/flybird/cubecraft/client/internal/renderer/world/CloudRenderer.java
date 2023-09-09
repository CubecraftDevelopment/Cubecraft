package ink.flybird.cubecraft.client.internal.renderer.world;

import com.google.gson.JsonElement;
import ink.flybird.quantum3d.BufferAllocation;
import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.quantum3d.draw.VertexBuilder;
import ink.flybird.quantum3d.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d.drawcall.IRenderCall;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.fcommon.ColorUtil;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import ink.flybird.quantum3d.drawcall.ListRenderCall;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import io.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TypeItem(WorldRendererType.CLOUD)
public class CloudRenderer extends IWorldRenderer {
    public static final int NO_CLOUD_RENDER = 0;
    public static final int FLAT_CLOUD_RENDER = 1;
    public static final int CUBIC_CLOUD_RENDER = 2;

    public static final float CLASSIC_LIGHT_Z = 0.8f;
    public static final float CLASSIC_LIGHT_X = 0.6f;

    private final IRenderCall[] renderLists = new IRenderCall[5];
    private final ByteBuffer cachedMapping;

    private Config cfg;

    public CloudRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
        for (int i = 0; i < 5; i++) {
            this.renderLists[i] = new ListRenderCall();
            this.renderLists[i].allocate();
        }
        this.cachedMapping = BufferAllocation.allocByteBuffer(512 * 512);
    }

    @Override
    public void refresh() {
        Synth synth = new PerlinNoise(new Random(world.getSeed()), 4);
        for (int x = 0; x < 512; x++) {
            for (int z = 0; z < 512; z++) {
                this.cachedMapping.put(x * 512 + z, (byte) synth.getValue(x, z));
            }
        }
    }

    @Override
    public void stop() {
        BufferAllocation.free(this.cachedMapping);
    }

    @Override
    public void config(JsonElement json) {
        this.cfg = new Config(
                MathHelper.hex2Int(json.getAsJsonObject().get("color").getAsString()),
                json.getAsJsonObject().get("thick").getAsFloat(),
                json.getAsJsonObject().get("size").getAsFloat(),
                json.getAsJsonObject().get("opacity").getAsFloat(),
                json.getAsJsonObject().get("use_classic_lighting").getAsBoolean(),
                json.getAsJsonObject().get("effect_by_world_light").getAsBoolean(),
                1 - json.getAsJsonObject().get("dense").getAsFloat(),
                json.getAsJsonObject().get("height").getAsFloat(),
                json.getAsJsonObject().get("motion_x").getAsFloat(),
                json.getAsJsonObject().get("motion_z").getAsFloat()
        );
        this.updateCloud();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }
        GLUtil.checkError("pre_cloud_render");
        this.parent.setRenderState(this.setting);
        camera.setUpGlobalCamera(window);
        camera.updateFrustum();
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_FOG);
        GLUtil.setupFog(this.setting.getValueAsInt("client.render.terrain.render_distance", 4) * 16 * 8 * this.setting.getValueAsInt("client.render.terrain.render_distance", 4) * 16 * 8, ColorUtil.int1Float1ToFloat4(0, 1));
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_CULL_FACE);
        int d2 = this.setting.getValueAsInt("client.render.terrain.render_distance", 4) * 16 * 8;
        int cloudQuality = this.setting.getValueAsInt("client.render.environment.cloud.quality", FLAT_CLOUD_RENDER);
        if (cloudQuality <= NO_CLOUD_RENDER) {
            return;
        }
        if (cloudQuality == FLAT_CLOUD_RENDER) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        List<Vector2d> list = new ArrayList<>();

        float cloudSize=this.cfg.cloudSize;
        float cloudRadius=cloudSize/2;


        double offX = world.getTime() * this.cfg.motionX;
        double offZ = world.getTime() * this.cfg.motionZ;
        double camX = camera.getPosition().x();
        double camZ = camera.getPosition().z();
        double x0 = (camX - d2 - offX), x1 = (camX + d2 - offX);
        double z0 = (camZ - d2 - offZ), z1 = (camZ + d2 - offZ);
        for (long i = (long) (x0 / cloudSize); i < x1 / cloudSize; i += 1) {
            for (long j = (long) (z0 / cloudSize); j < z1 / cloudSize; j += 1) {
                double cloudX = i * (int) cloudSize;
                double cloudY = this.cfg.cloudHeight;
                double cloudZ = j * (int) cloudSize;
                AABB cloudAABB = new AABB(cloudX, cloudY, cloudZ, cloudX + cloudSize, cloudY + this.cfg.cloudThick, cloudZ + cloudSize);
                if (this.shouldDiscard(i, j) || !this.camera.aabbInFrustum(cloudAABB)) {
                    continue;
                }
                list.add(new Vector2d(i,j));
            }
        }

        list.sort((o1, o2) -> {
            float centeredHeight = cfg.cloudHeight + cfg.cloudThick / 2;
            Vector3d vec1 = new Vector3d(o1.x * cloudSize + cloudRadius, centeredHeight, o1.y * cloudSize + cloudRadius);
            Vector3d vec2 = new Vector3d(o2.x * cloudSize + cloudRadius, centeredHeight, o2.y * cloudSize + cloudRadius);

            return -Double.compare(vec1.distance(this.camera.getPosition()),vec2.distance(this.camera.getPosition()));
        });


        for (Vector2d vec:list){
            GL11.glPushMatrix();
            long i= (long) vec.x();
            long j= (long) vec.y();

            double cloudX = i * (int) cloudSize;
            double cloudY = this.cfg.cloudHeight;
            double cloudZ = j * (int) cloudSize;

            camera.setupObjectCamera(new Vector3d(cloudX,cloudY,cloudZ));
            this.renderLists[0].call();
            if (this.shouldDiscard(i, j - 1)) {
                this.renderLists[1].call();
            }
            if (this.shouldDiscard(i, j + 1)) {
                this.renderLists[2].call();
            }
            if (this.shouldDiscard(i - 1, j)) {
                this.renderLists[3].call();
            }
            if (this.shouldDiscard(i + 1, j)) {
                this.renderLists[4].call();
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public void postRender(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.parent.closeState(this.setting);
        GLUtil.checkError("post_cloud_render");
    }

    public void updateCloud() {
        GLUtil.checkError("pre_cloud_update");
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(24);
        double x0 = (0);
        double x1 = (this.cfg.cloudSize);
        double y0 = (0);
        double y1 = (this.cfg.cloudThick);
        double z0 = (0);
        double z1 = (this.cfg.cloudSize);
        float[] col = ColorUtil.int1Float1ToFloat4(this.cfg.cloudColor, this.cfg.cloudOpacity);

        //draw top
        builder.begin();
        builder.color(col[0], col[1], col[2], col[3]);
        builder.vertex(x1, y1, z1);
        builder.vertex(x1, y1, z0);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y1, z1);
        builder.vertex(x0, y0, z1);
        builder.vertex(x0, y0, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y0, z1);
        builder.end();
        this.renderLists[0].upload(builder);


        float col0 = 1;
        if (this.cfg.useClassLighting) {
            col0 = CLASSIC_LIGHT_Z;
        }
        //draw back
        builder.begin();
        builder.color(col[0] * col0, col[1] * col0, col[2] * col0, col[3]);
        builder.vertex(x0, y1, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x1, y0, z0);
        builder.vertex(x0, y0, z0);
        builder.end();
        this.renderLists[1].upload(builder);

        //draw front
        builder.begin();
        builder.color(col[0] * col0, col[1] * col0, col[2] * col0, col[3]);
        builder.vertex(x0, y1, z1);
        builder.vertex(x0, y0, z1);
        builder.vertex(x1, y0, z1);
        builder.vertex(x1, y1, z1);
        builder.end();
        this.renderLists[2].upload(builder);

        if (this.cfg.useClassLighting) {
            col0 = CLASSIC_LIGHT_X;
        }
        //draw left
        builder.begin();
        builder.color(col[0] * col0, col[1] * col0, col[2] * col0, col[3]);
        builder.vertex(x0, y1, z1);
        builder.vertex(x0, y1, z0);
        builder.vertex(x0, y0, z0);
        builder.vertex(x0, y0, z1);
        builder.end();
        this.renderLists[3].upload(builder);

        //draw right
        builder.begin();
        builder.color(col[0] * col0, col[1] * col0, col[2] * col0, col[3]);
        builder.vertex(x1, y0, z1);
        builder.vertex(x1, y0, z0);
        builder.vertex(x1, y1, z0);
        builder.vertex(x1, y1, z1);
        builder.end();
        this.renderLists[4].upload(builder);
        GLUtil.checkError("post_cloud_update");
        builder.free();
    }

    public boolean shouldDiscard(long x, long z) {
        int fixX = (int) MathHelper.getRelativePosInChunk(x, 512);
        int fixZ = (int) MathHelper.getRelativePosInChunk(z, 512);
        int index = fixX * 512 + fixZ;

        return this.cachedMapping.get(index) <= 1;
    }

    record Config(
            int cloudColor,
            float cloudThick,
            float cloudSize,
            float cloudOpacity,
            boolean useClassLighting,
            boolean effectByWorldLight,

            float cloudDense,
            float cloudHeight,
            float motionX,
            float motionZ
    ) {
    }
}
