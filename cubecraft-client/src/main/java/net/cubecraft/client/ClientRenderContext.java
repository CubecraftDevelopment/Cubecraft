package net.cubecraft.client;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.client.render.model.ModelManager;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.render.model.block.IColorMap;
import net.cubecraft.client.render.model.object.EntityModel;
import net.cubecraft.client.render.renderer.IEntityRenderer;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.RegisterMap;
import me.gb2022.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.textures.TextureManager;

public interface ClientRenderContext {
    RegisterMap<IBlockRenderer> BLOCK_RENDERER = new RegisterMap<>();
    RegisterMap<IEntityRenderer> ENTITY_RENDERER = new RegisterMap<>();
    ConstructingMap<IWorldRenderer> WORLD_RENDERER = new ConstructingMap<>(IWorldRenderer.class, Window.class, IWorld.class, EntityPlayer.class, Camera.class);
    ConstructingMap<ChunkLayer> CHUNK_LAYER_RENDERER = new ConstructingMap<>(ChunkLayer.class, boolean.class, RenderChunkPos.class);

    ModelManager<BlockModel> BLOCK_MODEL = new ModelManager<>(BlockModel.class, new ModelAsset("cubecraft:/block/fallback.json"));
    ModelManager<EntityModel> ENTITY_MODEL = new ModelManager<>(EntityModel.class, null);//todo:fallback

    TextureManager TEXTURE = new TextureManager();
    RegisterMap<IColorMap> COLOR_MAP = new RegisterMap<>();
}