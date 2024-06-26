package net.cubecraft.client.context;

import ink.flybird.quantum3d_legacy.textures.TextureManager;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.RegisterMap;
import me.gb2022.quantum3d.lwjgl.FrameBuffer;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.ClientRenderContextInitEvent;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.client.render.model.ModelManager;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.render.model.IColorMap;
import net.cubecraft.client.render.model.object.EntityModel;
import net.cubecraft.client.render.renderer.IEntityRenderer;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.IWorld;

public class ClientRenderContext extends ClientContext {
    public static final RegisterMap<IBlockRenderer> BLOCK_RENDERER = new RegisterMap<>(IBlockRenderer.class);
    public static final RegisterMap<IEntityRenderer> ENTITY_RENDERER = new RegisterMap<>(IEntityRenderer.class);
    public static final ConstructingMap<IWorldRenderer> WORLD_RENDERER = new ConstructingMap<>(IWorldRenderer.class);
    public static final ConstructingMap<ChunkLayer> CHUNK_LAYER_RENDERER = new ConstructingMap<>(ChunkLayer.class, boolean.class, RenderChunkPos.class);
    public static final ModelManager<BlockModel> BLOCK_MODEL = new ModelManager<>(BlockModel.class, new ModelAsset("cubecraft:/block/fallback.json"));
    public static final ModelManager<EntityModel> ENTITY_MODEL = new ModelManager<>(EntityModel.class, null);//todo:fallback
    public static final TextureManager TEXTURE = new TextureManager();
    public static final RegisterMap<IColorMap> COLOR_MAP = new RegisterMap<>(IColorMap.class);

    private final FrameBuffer buffer = new FrameBuffer();

    private LevelRenderer levelRenderer;

    public ClientRenderContext(CubecraftClient client) {
        super(client);
    }

    @Override
    public void init() {
        this.client.getClientEventBus().callEvent(new ClientRenderContextInitEvent(this.client, this));
    }

    @Override
    public void joinWorld(IWorld world) {
        ClientWorldContext context = this.client.getClientWorldContext();

        ResourceLocation loc = ResourceLocation.worldRendererSetting(world.getId() + ".json");
        this.levelRenderer = new LevelRenderer(world, context.getPlayer(), loc);
    }

    @Override
    public void leaveLevel() {
        this.levelRenderer.stop();
        this.levelRenderer = null;
    }

    @Override
    public void tick() {
        if (this.levelRenderer != null) {
            this.levelRenderer.tick();
        }
    }

    public void render(float delta) {
        //this.buffer.resize(this.client.getWindow().getWidth(), this.client.getWindow().getHeight());
        this.levelRenderer.render(delta);
    }

    public FrameBuffer getBuffer() {
        return buffer;
    }
}
