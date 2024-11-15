package net.cubecraft.client.render.chunk.container;

import me.gb2022.quantum3d.texture.Texture2DTileMap;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.chunk.TerrainRenderer;
import net.cubecraft.util.register.Named;
import net.cubecraft.util.register.Registered;

public interface ChunkLayerContainerFactory {
    ChunkLayerContainer createChunkLayer(TerrainRenderer renderer, int viewRange, boolean vbo);

    final class Provider implements Named {
        private final ChunkLayerContainerFactory chunkLayerContainerFactory;
        private final RenderType renderType;
        private final String id;
        private final Registered<Texture2DTileMap> texture;
        private final boolean chunked;

        public Provider(ChunkLayerContainerFactory chunkLayerContainerFactory, RenderType renderType, String id, Registered<Texture2DTileMap> texture, boolean chunked) {
            this.chunkLayerContainerFactory = chunkLayerContainerFactory;
            this.renderType = renderType;
            this.id = id;
            this.texture = texture;
            this.chunked = chunked;
        }

        public ChunkLayerContainerFactory getFactory() {
            return chunkLayerContainerFactory;
        }

        public RenderType getRenderType() {
            return renderType;
        }

        @Override
        public String getName() {
            return id;
        }

        public Texture2DTileMap getTextureUsed() {
            return this.texture.get();
        }

        public Registered<Texture2DTileMap> getTexture() {
            return texture;
        }

        public boolean isChunked() {
            return chunked;
        }
    }
}
