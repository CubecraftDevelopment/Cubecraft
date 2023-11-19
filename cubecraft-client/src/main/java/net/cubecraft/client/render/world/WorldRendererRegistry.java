package net.cubecraft.client.render.world;

import net.cubecraft.client.render.chunk.ChunkRenderer;
import ink.flybird.fcommon.registry.ClassRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface WorldRendererRegistry {
    @ClassRegistry
    Class<? extends IWorldRenderer> CHUNK_RENDERER = ChunkRenderer.class;

    @ClassRegistry
    Class<? extends IWorldRenderer> CLOUD_RENDERER = CloudRenderer.class;

    @ClassRegistry
    Class<? extends IWorldRenderer> ENTITY_RENDERER = EntityRenderer.class;

    @ClassRegistry
    Class<? extends IWorldRenderer> HUD_RENDERER = HUDRenderer.class;

    @ClassRegistry
    Class<? extends IWorldRenderer> SKY_BOX_RENDERER = SkyBoxRenderer.class;

    @ClassRegistry
    Class<? extends IWorldRenderer> PARTICLE_RENDERER = ParticleRenderer.class;
}