package net.cubecraft.client.render.chunk.container;

import net.cubecraft.client.render.Textures;
import net.cubecraft.client.render.chunk.TerrainRenderer;

public final class AlphaCutoutContainer extends AlphaBlockContainer {
    public AlphaCutoutContainer(TerrainRenderer parent, int viewRange, boolean useVBO) {
        super(parent, viewRange, useVBO);
    }

    @Override
    public int getId() {
        return ChunkLayerContainers.ALPHA_CUTOUT.getId();
    }

    @Override
    public void setup() {
        super.setup();
        Textures.TERRAIN_CUTOUT.get().bind();
    }
}
