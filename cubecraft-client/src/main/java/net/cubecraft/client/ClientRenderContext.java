package net.cubecraft.client;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.RegisterMap;
import me.gb2022.quantum3d.texture.TextureManager;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.render.model.ModelManager;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.render.model.object.EntityModel;
import net.cubecraft.client.render.renderer.IEntityRenderer;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.util.register.ShadowedRegistry;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.Blocks;

public interface ClientRenderContext {
    ShadowedRegistry<IBlockRenderer, Block> BLOCK_RENDERERS = new ShadowedRegistry<>(Blocks.REGISTRY);

    RegisterMap<IEntityRenderer> ENTITY_RENDERER = new RegisterMap<>(IEntityRenderer.class);
    ConstructingMap<IWorldRenderer> WORLD_RENDERER = new ConstructingMap<>(IWorldRenderer.class);
    ModelManager<BlockModel> BLOCK_MODEL = new ModelManager<>(BlockModel.class, new ModelAsset("cubecraft:/block/fallback.json"));
    ModelManager<EntityModel> ENTITY_MODEL = new ModelManager<>(EntityModel.class, null);//todo:fallback
    TextureManager TEXTURE = new TextureManager();
    RegisterMap<ColorMap> COLOR_MAP = new RegisterMap<>(ColorMap.class);
}
