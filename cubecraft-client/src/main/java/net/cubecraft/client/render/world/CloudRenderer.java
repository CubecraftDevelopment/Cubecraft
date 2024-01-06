package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.quantum3d.ColorElement;
import me.gb2022.quantum3d.lwjgl.batching.GLRenderList;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.world.worldGen.noiseGenerator.Noise;
import net.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//todo:区块压缩16个section共享一张表并整体传输

@TypeItem(WorldRendererType.CLOUD)
public class CloudRenderer extends IWorldRenderer {
    public static final VertexBuilderAllocator ALLOCATOR = new VertexBuilderAllocator(LevelRenderer.ALLOCATOR);

    public static final float CLASSIC_LIGHT_Z = 0.8f;
    public static final float CLASSIC_LIGHT_X = 0.6f;

    private final GLRenderList[] renderLists = new GLRenderList[6];
    private ByteBuffer cachedMapping;
    private Config cfg;

    private ColorElement cloudColor;

    @Override
    public void init() {
        this.cachedMapping = BufferAllocation.allocByteBuffer(512 * 512);
        Noise synth = new PerlinNoise(new Random(world.getSeed()), 4);
        for (int x = 0; x < 512; x++) {
            for (int z = 0; z < 512; z++) {
                this.cachedMapping.put(x * 512 + z, (byte) synth.getValue(x, z));
            }
        }
        for (int i = 0; i < 6; i++) {
            this.renderLists[i] = new GLRenderList();
            this.renderLists[i].allocate();
        }
        this.updateCloud();
    }

    @Override
    public void stop() {
        if (this.renderLists[0] != null) {
            for (int i = 0; i < 6; i++) {
                this.renderLists[i].free();
            }
        }
        if (this.cachedMapping != null) {
            BufferAllocation.free(this.cachedMapping);
        }
    }

    @Override
    public void config(JsonObject json) {
        this.cloudColor = ColorElement.parseFromString(json.getAsJsonObject().get("color").getAsString());

        this.cfg = new Config(
                MathHelper.hex2Int(json.getAsJsonObject().get("color").getAsString()),
                json.getAsJsonObject().get("thick").getAsFloat(),
                json.getAsJsonObject().get("size").getAsFloat(),
                json.getAsJsonObject().get("use_classic_lighting").getAsBoolean(),
                json.getAsJsonObject().get("effect_by_world_light").getAsBoolean(),
                1 - json.getAsJsonObject().get("dense").getAsFloat(),
                json.getAsJsonObject().get("height").getAsFloat(),
                json.getAsJsonObject().get("speed_x").getAsFloat(),
                json.getAsJsonObject().get("speed_z").getAsFloat()
        );
    }

    @Override
    public void preRender(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }
        GLUtil.checkError("pre_cloud_render");
        camera.setUpGlobalCamera();
        camera.updateFrustum();
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }

        int d2 = ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16 * 10;

        List<Vector2d> list = new ArrayList<>();

        float cloudSize = this.cfg.cloudSize;
        float cloudRadius = cloudSize / 2;


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
                list.add(new Vector2d(i, j));
            }
        }

        list.sort((o1, o2) -> {
            float centeredHeight = cfg.cloudHeight + cfg.cloudThick / 2;
            Vector3d vec1 = new Vector3d(o1.x * cloudSize + cloudRadius, centeredHeight, o1.y * cloudSize + cloudRadius);
            Vector3d vec2 = new Vector3d(o2.x * cloudSize + cloudRadius, centeredHeight, o2.y * cloudSize + cloudRadius);

            return -Double.compare(vec1.distance(this.camera.getPosition()), vec2.distance(this.camera.getPosition()));
        });

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);


        for (Vector2d vec : list) {
            GL11.glPushMatrix();
            long i = (long) vec.x();
            long j = (long) vec.y();

            double cloudX = i * (int) cloudSize;
            double cloudY = this.cfg.cloudHeight;
            double cloudZ = j * (int) cloudSize;

            camera.setupObjectCamera(new Vector3d(cloudX, cloudY, cloudZ));
            this.renderLists[0].call();
            this.renderLists[1].call();
            if (this.shouldDiscard(i, j - 1)) {
                this.renderLists[2].call();
            }
            if (this.shouldDiscard(i, j + 1)) {
                this.renderLists[3].call();
            }
            if (this.shouldDiscard(i - 1, j)) {
                this.renderLists[4].call();
            }
            if (this.shouldDiscard(i + 1, j)) {
                this.renderLists[5].call();
            }
            GL11.glPopMatrix();
        }

        double d3 = d2 * cloudSize;
        double a = Math.sqrt(d3) * d3;
        GLUtil.setupFog(a, this.parent.getFogColor());

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void postRender(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }

        GLUtil.checkError("post_cloud_render");
    }

    public void updateCloud() {
        GLUtil.checkError("pre_cloud_update");
        for (int i = 0; i < 6; i++) {
            this.generateData(i);
        }
    }

    public void generateData(int face) {
        double x0 = (0);
        double x1 = (this.cfg.cloudSize);
        double y0 = (0);
        double y1 = (this.cfg.cloudThick);
        double z0 = (0);
        double z1 = (this.cfg.cloudSize);
        double[] col = this.cloudColor.RGBA_F();

        VertexBuilder builder = ALLOCATOR.allocate(VertexFormat.V3F_C4F, DrawMode.QUADS, 4);
        builder.allocate();

        float col0 = 1.0f;

        switch (face) {
            case 2, 3 -> {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_Z;
                }
            }
            case 4, 5 -> {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_X;
                }
            }
        }

        builder.setColor(col[0] * col0, col[1] * col0, col[2] * col0, 0.7f);

        switch (face) {
            case 0 -> {
                builder.addVertex(x1, y1, z1);
                builder.addVertex(x1, y1, z0);
                builder.addVertex(x0, y1, z0);
                builder.addVertex(x0, y1, z1);
            }
            case 1 -> {
                builder.addVertex(x0, y0, z1);
                builder.addVertex(x0, y0, z0);
                builder.addVertex(x1, y0, z0);
                builder.addVertex(x1, y0, z1);
            }
            case 2 -> {
                builder.addVertex(x0, y1, z0);
                builder.addVertex(x1, y1, z0);
                builder.addVertex(x1, y0, z0);
                builder.addVertex(x0, y0, z0);
            }
            case 3 -> {
                builder.addVertex(x0, y1, z1);
                builder.addVertex(x0, y0, z1);
                builder.addVertex(x1, y0, z1);
                builder.addVertex(x1, y1, z1);
            }
            case 4 -> {
                builder.addVertex(x0, y1, z1);
                builder.addVertex(x0, y1, z0);
                builder.addVertex(x0, y0, z0);
                builder.addVertex(x0, y0, z1);
            }
            case 5 -> {
                builder.addVertex(x1, y0, z1);
                builder.addVertex(x1, y0, z0);
                builder.addVertex(x1, y1, z0);
                builder.addVertex(x1, y1, z1);
            }
        }

        this.renderLists[face].upload(builder);
        ALLOCATOR.free(builder);
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
            boolean useClassLighting,
            boolean effectByWorldLight,

            float cloudDense,
            float cloudHeight,
            float motionX,
            float motionZ
    ) {
    }
}
