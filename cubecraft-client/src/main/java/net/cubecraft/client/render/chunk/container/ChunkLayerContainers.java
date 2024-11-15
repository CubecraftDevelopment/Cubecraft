package net.cubecraft.client.render.chunk.container;

import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.Textures;
import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.util.register.Registered;

public interface ChunkLayerContainers {
    NamedRegistry<ChunkLayerContainerFactory.Provider> REGISTRY = new NamedRegistry<>();

    Registered<ChunkLayerContainerFactory.Provider> ALPHA_BLOCK = REGISTRY.register(new ChunkLayerContainerFactory.Provider(
            AlphaBlockContainer::new,
            RenderType.ALPHA,
            "cubecraft:alpha_block",
            Textures.TERRAIN_SIMPLE,
            false
    ));

    Registered<ChunkLayerContainerFactory.Provider> ALPHA_CUTOUT = REGISTRY.register(new ChunkLayerContainerFactory.Provider(
            AlphaCutoutContainer::new,
            RenderType.ALPHA,
            "cubecraft:alpha_cutout",
            Textures.TERRAIN_CUTOUT,
            false//todo
    ));

    Registered<ChunkLayerContainerFactory.Provider> TRANSPARENT = REGISTRY.register(new ChunkLayerContainerFactory.Provider(
            TransparentBlockContainer::new,
            RenderType.TRANSPARENT,
            "cubecraft:transparent_block",
            Textures.TERRAIN_SIMPLE,
            false
    ));

}
