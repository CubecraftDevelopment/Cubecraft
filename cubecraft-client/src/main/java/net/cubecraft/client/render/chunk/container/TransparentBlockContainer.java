package net.cubecraft.client.render.chunk.container;

import net.cubecraft.client.render.Textures;
import net.cubecraft.client.render.chunk.TerrainRenderer;

public class TransparentBlockContainer extends ChunkLayerContainer {


    protected TransparentBlockContainer(TerrainRenderer parent, int viewRange, boolean useVBO) {
        super(parent, viewRange, useVBO);
    }

    @Override
    public void setup() {
        var a = Textures.TERRAIN_TRANSPARENT.get();
        a.bind();
    }

    @Override
    public int getId() {
        return ChunkLayerContainers.TRANSPARENT.getId();
    }
}
