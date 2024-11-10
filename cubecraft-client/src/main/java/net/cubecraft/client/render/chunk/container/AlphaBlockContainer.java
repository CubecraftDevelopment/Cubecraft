package net.cubecraft.client.render.chunk.container;

import net.cubecraft.client.render.Textures;
import net.cubecraft.client.render.chunk.TerrainRenderer;
import org.lwjgl.opengl.GL11;

public class AlphaBlockContainer extends ChunkLayerContainer {
    protected AlphaBlockContainer(TerrainRenderer parent, int viewRange, boolean useVBO) {
        super(parent, viewRange, useVBO);
    }

    @Override
    public void setup() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);

        Textures.TERRAIN_SIMPLE.get().bind();
    }

    @Override
    public int getId() {
        return ChunkLayerContainers.ALPHA_BLOCK.getId();
    }
}