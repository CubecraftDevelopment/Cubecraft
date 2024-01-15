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
    private int cloudRenderDistance;

    private CloudRegionCache regionCache;

    private long cameraGridX = Long.MIN_VALUE;
    private long cameraGridZ = Long.MIN_VALUE;

    //cx += (long) (world.getTime() * 1);
    //cz += (long) (world.getTime() * 1);

    @Override
    public void init() {
        this.cachedMapping = BufferAllocation.allocByteBuffer(512 * 512);
        for (int i = 0; i < 6; i++) {
            this.renderLists[i] = new GLRenderList();
            this.renderLists[i].allocate();
        }

        for (int i = 0; i < 6; i++) {
            this.generateData(i);
        }

        this.cloudRenderDistance = ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16 * 10;
        this.regionCache = new CloudRegionCache(this);

        long cx = (long) (this.getCamera().getPosition().x / this.cfg.cloudSize);
        long cz = (long) (this.getCamera().getPosition().z / this.cfg.cloudSize);
        this.cameraGridX = cx;
        this.cameraGridZ = cz;
        this.regionCache.moveTo(cx, cz);
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
        this.parent.setFog(ClientSettingRegistry.getFixedViewDistance() * 16 * 10);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }

        int d2 = this.cloudRenderDistance;
        List<Vector2d> list = new ArrayList<>();

        float cloudSize = this.cfg.cloudSize;
        float cloudRadius = cloudSize / 2;

        int range = (int) ((ClientSettingRegistry.getFixedViewDistance() * 16 * 5) / cloudSize);

        long camCX = this.cameraGridX;
        long camCZ = this.cameraGridZ;


        for (long cx = camCX - range; cx < camCX + range; cx++) {
            for (long cz = camCZ - range; cz < camCZ + range; cz++) {
                if (!this.regionCache.getValue(cx, cz)) {
                    continue;
                }
                double cloudX = cx * (int) cloudSize;
                double cloudZ = cz * (int) cloudSize;
                double cloudY = this.cfg.cloudHeight;
                AABB cloudAABB = new AABB(cloudX, cloudY, cloudZ, cloudX + cloudSize, cloudY + this.cfg.cloudThick, cloudZ + cloudSize);
                if (!this.camera.aabbInFrustum(cloudAABB)) {
                    continue;
                }
                list.add(new Vector2d(cx, cz));
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
    public void tick() {
        long cx = (long) (this.getCamera().getPosition().x / this.cfg.cloudSize);
        long cz = (long) (this.getCamera().getPosition().z / this.cfg.cloudSize);

        if (this.cameraGridX != cx || this.cameraGridZ != cz) {
            this.cameraGridX = cx;
            this.cameraGridZ = cz;
            this.regionCache.moveTo(cx, cz);
        }
    }

    @Override
    public void postRender(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }
        GLUtil.checkError("post_cloud_render");
    }

    public void generateData(int face) {
        double x0 = (0);
        double x1 = (this.cfg.cloudSize);
        double y0 = (0);
        double y1 = (this.cfg.cloudThick);
        double z0 = (0);
        double z1 = (this.cfg.cloudSize);
        float[] col = this.cloudColor.RGBA_F();

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
        return !this.regionCache.getValue(x, z);
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

    private static class CloudRegionCache {
        private final CloudRenderer parent;
        private final PerlinNoise noise;
        private final boolean[] noiseCache;
        private final boolean[] noiseCacheCopied;
        private final int cloudRenderDistance;
        private final int dataSize;
        private long centerX = Long.MIN_VALUE + 1024;
        private long centerZ = Long.MIN_VALUE + 1024;

        public CloudRegionCache(CloudRenderer parent) {
            this.parent = parent;
            this.cloudRenderDistance = (int) ((ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16 * 5) / this.parent.cfg.cloudSize) + 1;
            this.dataSize = this.cloudRenderDistance * 2 + 1;
            this.noiseCache = new boolean[dataSize * dataSize];
            this.noiseCacheCopied = new boolean[dataSize * dataSize];
            this.noise = new PerlinNoise(new Random(parent.world.getSeed()), 3);

            for (int x = 0; x < this.dataSize; x++) {
                for (int z = 0; z < this.dataSize; z++) {
                    this.noiseCache[toArrayPos(x, z)] = generateNoiseValue(toAbsX(x), toAbsZ(z));
                }
            }
        }

        public int toArrayPos(int x, int z) {
            return x * this.dataSize + z;
        }

        public boolean generateNoiseValue(long x, long z) {
            return this.noise.getValue(x, z) > 0.8;
        }

        public void moveTo(long centerX, long centerZ) {
            if (centerX == this.centerX && centerZ == this.centerZ) {
                return;
            }

            int offsetX = (int) (centerX - this.centerX);
            int offsetZ = (int) (centerZ - this.centerZ);

            this.centerX = centerX;
            this.centerZ = centerZ;

            System.arraycopy(this.noiseCache, 0, this.noiseCacheCopied, 0, this.dataSize * this.dataSize);

            for (int x = 0; x < this.dataSize; x++) {
                for (int z = 0; z < this.dataSize; z++) {
                    int newX = x + offsetX;
                    int newZ = z + offsetZ;

                    if (newX >= 0 && newX < this.dataSize
                            && newZ >= 0 && newZ < this.dataSize) {
                        this.noiseCache[toArrayPos(x, z)] = this.noiseCacheCopied[toArrayPos(newX, newZ)];
                    } else {
                        this.noiseCache[toArrayPos(x, z)] = generateNoiseValue(toAbsX(x), toAbsZ(z));
                    }
                }
            }
        }

        public long toAbsX(int x) {
            return x + this.centerX - this.cloudRenderDistance - 2;
        }

        public long toAbsZ(int z) {
            return z + this.centerZ - this.cloudRenderDistance - 2;
        }

        public int toRelX(long x) {
            return (int) (x - this.centerX + this.cloudRenderDistance + 2);
        }

        public int toRelZ(long z) {
            return (int) (z - this.centerZ + this.cloudRenderDistance + 2);
        }

        public boolean getValue(long x, long z) {
            int x2 = toRelX(x);
            int z2 = toRelZ(z);
            if (x2 < 0 || x2 >= this.dataSize || z2 < 0 || z2 >= this.dataSize) {
                return true;
            }

            return this.noiseCache[toArrayPos(x2, z2)];
        }
    }
}

