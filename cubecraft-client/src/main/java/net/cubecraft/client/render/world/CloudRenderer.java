package net.cubecraft.client.render.world;

import com.google.gson.JsonObject;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.ColorElement;
import me.gb2022.quantum3d.lwjgl.batching.GLRenderList;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.camera.ViewFrustum;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.world.worldGen.noise.PerlinNoise;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@TypeItem(WorldRendererType.CLOUD)
public final class CloudRenderer extends IWorldRenderer {
    public static final VertexBuilderAllocator ALLOCATOR = new VertexBuilderAllocator(LevelRenderer.ALLOCATOR);

    public static final float CLASSIC_LIGHT_Z = 0.8f;
    public static final float CLASSIC_LIGHT_X = 0.6f;
    private final GLRenderList[] models = new GLRenderList[16];
    private final GLRenderList flat = new GLRenderList();
    private final ViewFrustum frustum = new ViewFrustum();
    private CloudRegionCache regionCache;
    private Config cfg;
    private ColorElement cloudColor;
    private int cloudRenderDistance;
    private boolean flatCloud;

    private long cameraGridX = Long.MIN_VALUE;
    private long cameraGridZ = Long.MIN_VALUE;

    @Override
    public void init() {
        for (int i = 0; i < 16; i++) {
            this.models[i] = new GLRenderList();
            this.models[i].allocate();
            this.generateData(i);
        }

        this.flat.allocate();
        this.generateData(16);

        this.cloudRenderDistance = ClientSettings.getFixedViewDistance() * 16 * 10;
        this.regionCache = new CloudRegionCache(this);

        long cx = (long) (this.viewCamera.getX() / this.cfg.cloudSize);
        long cz = (long) (this.viewCamera.getZ() / this.cfg.cloudSize);
        this.cameraGridX = cx;
        this.cameraGridZ = cz;
        this.regionCache.moveTo(cx, cz);
        this.flatCloud = ClientSettings.RenderSetting.WorldSetting.CloudSetting.QUALITY.getValue() < 2;
    }

    @Override
    public void stop() {
        for (var fl : this.models) {
            fl.free();
        }
        this.flat.free();
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
        if (type != RenderType.TRANSPARENT) {
            return;
        }

        this.frustum.update(this.viewCamera);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if (this.flatCloud) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.TRANSPARENT) {
            return;
        }
        if (!ClientSettings.RenderSetting.WorldSetting.CloudSetting.ENABLE.getValue()) {
            return;
        }

        var d2 = this.cloudRenderDistance;
        var size = this.cfg.cloudSize;
        var radius = size / 2;
        int range = (int) ((ClientSettings.getFixedViewDistance() * 16 * 5) / size) - 3;

        var ox = 0;
        var oz = this.world.getTime() * 0.05f;

        var gx = (long) (this.cameraGridX + ox / size);
        var gz = (long) (this.cameraGridZ + oz / size);

        for (var x = gx - range; x <= gx + range; x++) {
            for (var z = gz - range; z <= gz + range; z++) {
                if (!this.regionCache.getValue(x, z)) {
                    continue;
                }

                var cx = x * (int) size;
                var cz = z * (int) size;
                var cy = this.cfg.cloudHeight;

                var x0 = cx - radius - ox;
                var z0 = cz - radius - oz;
                var x1 = cx + radius - ox;
                var z1 = cz + radius - oz;

                var aabb = new AABB(x0, cy - radius, z0, x1, cy + this.cfg.cloudThick + radius, z1);

                if (!this.frustum.boxVisible(aabb)) {
                    continue;
                }

                renderCloudsAt(x, z, size, ox, oz);
            }
        }
        double d3 = d2 * size;
        double a = Math.sqrt(d3) * d3;
        GLUtil.setupFog(a, this.parent.getFogColor());
    }

    private void renderCloudsAt(long i, long j, float size, double ox, double oz) {
        double x = i * (int) size;
        double y = this.cfg.cloudHeight;
        double z = j * (int) size;

        this.viewCamera.push().object(new Vector3d(x - ox, y, z - oz)).set();

        if (this.flatCloud) {
            this.flat.call();
            this.viewCamera.pop();
            return;
        }

        var mid = 15;

        if (this.visible(i, j - 1)) {
            mid &= ~(1);
        }
        if (this.visible(i, j + 1)) {
            mid &= ~(1 << 1);
        }
        if (this.visible(i - 1, j)) {
            mid &= ~(1 << 2);
        }
        if (this.visible(i + 1, j)) {
            mid &= ~(1 << 3);
        }

        this.models[mid].call();

        this.viewCamera.pop();
    }

    @Override
    public void tick() {
        long cx = (long) (this.viewCamera.getX() / this.cfg.cloudSize);
        long cz = (long) (this.viewCamera.getZ() / this.cfg.cloudSize);

        if (this.cameraGridX != cx || this.cameraGridZ != cz) {
            this.cameraGridX = cx;
            this.cameraGridZ = cz;
            this.regionCache.moveTo(cx, cz);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public void generateData(int mid) {
        VertexBuilder builder = ALLOCATOR.create(VertexFormat.V3F_C4F, DrawMode.QUADS, 32);
        builder.allocate();

        var x0 = (0);
        var x1 = (this.cfg.cloudSize);
        var y0 = (0);
        var y1 = (this.cfg.cloudThick);
        var z0 = (0);
        var z1 = (this.cfg.cloudSize);
        var col = this.cloudColor.RGBA_F();

        float col0 = 1.0f;

        builder.addVertex(x1, y1, z1);
        builder.addVertex(x1, y1, z0);
        builder.addVertex(x0, y1, z0);
        builder.addVertex(x0, y1, z1);

        if (mid != 16) {
            builder.addVertex(x0, y0, z1);
            builder.addVertex(x0, y0, z0);
            builder.addVertex(x1, y0, z0);
            builder.addVertex(x1, y0, z1);

            if (((mid) & 1) == 1) {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_Z;
                }

                builder.setColor(col[0] * col0, col[1] * col0, col[2] * col0, 0.7f);
                builder.addVertex(x0, y1, z0);
                builder.addVertex(x1, y1, z0);
                builder.addVertex(x1, y0, z0);
                builder.addVertex(x0, y0, z0);
            }
            if (((mid >> 1) & 1) == 1) {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_Z;
                }

                builder.setColor(col[0] * col0, col[1] * col0, col[2] * col0, 0.7f);
                builder.addVertex(x0, y1, z1);
                builder.addVertex(x0, y0, z1);
                builder.addVertex(x1, y0, z1);
                builder.addVertex(x1, y1, z1);
            }
            if (((mid >> 2) & 1) == 1) {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_X;
                }

                builder.setColor(col[0] * col0, col[1] * col0, col[2] * col0, 0.7f);
                builder.addVertex(x0, y1, z1);
                builder.addVertex(x0, y1, z0);
                builder.addVertex(x0, y0, z0);
                builder.addVertex(x0, y0, z1);
            }
            if (((mid >> 3) & 1) == 1) {
                if (this.cfg.useClassLighting) {
                    col0 = CLASSIC_LIGHT_X;
                }

                builder.setColor(col[0] * col0, col[1] * col0, col[2] * col0, 0.7f);

                builder.addVertex(x1, y0, z1);
                builder.addVertex(x1, y0, z0);
                builder.addVertex(x1, y1, z0);
                builder.addVertex(x1, y1, z1);
            }
        }

        if (mid != 16) {
            this.models[mid].upload(builder);
        } else {
            this.flat.upload(builder);
        }

        ALLOCATOR.free(builder);
    }

    public boolean visible(long x, long z) {
        return this.regionCache.getValue(x, z);
    }

    record Config(int cloudColor, float cloudThick, float cloudSize, boolean useClassLighting, boolean effectByWorldLight, float cloudDense,
                  float cloudHeight, float motionX, float motionZ) {
    }

    private static class CloudRegionCache {
        private final PerlinNoise noise;
        private final boolean[] noiseCache;
        private final boolean[] noiseCacheCopied;
        private final int cloudRenderDistance;
        private final int dataSize;
        private long centerX = 0;
        private long centerZ = 0;

        public CloudRegionCache(CloudRenderer parent) {
            this.cloudRenderDistance = (int) ((ClientSettings.getFixedViewDistance() * 16 * 5) / parent.cfg.cloudSize) + 4;
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

                    if (newX >= 0 && newX < this.dataSize && newZ >= 0 && newZ < this.dataSize) {
                        this.noiseCache[toArrayPos(x, z)] = this.noiseCacheCopied[toArrayPos(newX, newZ)];
                    } else {
                        this.noiseCache[toArrayPos(x, z)] = generateNoiseValue(toAbsX(x), toAbsZ(z));
                    }
                }
            }
        }

        public long toAbsX(int x) {
            return x + this.centerX - this.cloudRenderDistance;
        }

        public long toAbsZ(int z) {
            return z + this.centerZ - this.cloudRenderDistance;
        }

        public int toRelX(long x) {
            return (int) (x - this.centerX + this.cloudRenderDistance);
        }

        public int toRelZ(long z) {
            return (int) (z - this.centerZ + this.cloudRenderDistance);
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

