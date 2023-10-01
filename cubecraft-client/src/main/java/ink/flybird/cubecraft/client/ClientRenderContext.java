package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.client.render.block.IBlockRenderer;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.client.render.model.ModelManager;
import ink.flybird.cubecraft.client.render.model.block.BlockModel;
import ink.flybird.cubecraft.client.render.model.block.IColorMap;
import ink.flybird.cubecraft.client.render.model.object.EntityModel;
import ink.flybird.cubecraft.client.render.renderer.IEntityRenderer;
import ink.flybird.cubecraft.client.render.world.IWorldRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.RegisterMap;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.textures.TextureManager;

public interface ClientRenderContext {
    RegisterMap<IBlockRenderer, ?> BLOCK_RENDERER = new RegisterMap<>();
    RegisterMap<IEntityRenderer, ?> ENTITY_RENDERER = new RegisterMap<>();
    ConstructingMap<IWorldRenderer> WORLD_RENDERER = new ConstructingMap<>(IWorldRenderer.class, Window.class, IWorld.class, EntityPlayer.class, Camera.class, GameSetting.class);
    ConstructingMap<ChunkLayer> CHUNK_LAYER_RENDERER = new ConstructingMap<>(ChunkLayer.class, boolean.class, RenderChunkPos.class);

    ModelManager<BlockModel> BLOCK_MODEL = new ModelManager<>(BlockModel.class, ResourceLocation.blockModel("cubecraft:fallback.json"));
    ModelManager<EntityModel> ENTITY_MODEL = new ModelManager<>(EntityModel.class, null);//todo:fallback

    TextureManager TEXTURE = new TextureManager();
    RegisterMap<IColorMap, ?> COLOR_MAP = new RegisterMap<>();
}

//todo fix:重载区块假死问题