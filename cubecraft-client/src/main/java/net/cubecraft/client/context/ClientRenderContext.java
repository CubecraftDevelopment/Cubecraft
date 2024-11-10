package net.cubecraft.client.context;

import ink.flybird.quantum3d_legacy.textures.TextureManager;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.RegisterMap;
import me.gb2022.quantum3d.lwjgl.FrameBuffer;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.ClientRenderContextInitEvent;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.render.model.ModelManager;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.render.model.object.EntityModel;
import net.cubecraft.client.render.renderer.IEntityRenderer;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.util.register.ShadowedRegistry;
import net.cubecraft.world.World;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.Blocks;

public class ClientRenderContext extends ClientContext {
    public static final ShadowedRegistry<IBlockRenderer, Block> BLOCK_RENDERERS = new ShadowedRegistry<>(Blocks.REGISTRY);




    public static final RegisterMap<IEntityRenderer> ENTITY_RENDERER = new RegisterMap<>(IEntityRenderer.class);
    public static final ConstructingMap<IWorldRenderer> WORLD_RENDERER = new ConstructingMap<>(IWorldRenderer.class);
    public static final ModelManager<BlockModel> BLOCK_MODEL = new ModelManager<>(
            BlockModel.class,
            new ModelAsset("cubecraft:/block/fallback.json")
    );
    public static final ModelManager<EntityModel> ENTITY_MODEL = new ModelManager<>(EntityModel.class, null);//todo:fallback
    public static final TextureManager TEXTURE = new TextureManager();
    public static final RegisterMap<ColorMap> COLOR_MAP = new RegisterMap<>(ColorMap.class);

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
    public void joinWorld(World world) {
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
