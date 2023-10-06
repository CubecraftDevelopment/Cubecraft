package ink.flybird.cubecraft.client.internal;

import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.app.ClientDisposeEvent;
import ink.flybird.cubecraft.client.event.app.ClientSetupEvent;
import ink.flybird.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import ink.flybird.cubecraft.client.gui.GUIRegistry;
import ink.flybird.cubecraft.client.gui.layout.FlowLayout;
import ink.flybird.cubecraft.client.gui.layout.OriginLayout;
import ink.flybird.cubecraft.client.gui.layout.ViewportLayout;
import ink.flybird.cubecraft.client.gui.node.*;
import ink.flybird.cubecraft.client.internal.entity.BlockBrakeParticle;
import ink.flybird.cubecraft.client.internal.handler.ClientAssetLoader;
import ink.flybird.cubecraft.client.internal.handler.ClientListener;
import ink.flybird.cubecraft.client.internal.handler.ParticleHandler;
import ink.flybird.cubecraft.client.internal.handler.ScreenController;
import ink.flybird.cubecraft.client.registry.ClientNetworkHandlerRegistry;
import ink.flybird.cubecraft.client.registry.ColorMapRegistry;
import ink.flybird.cubecraft.client.registry.RenderRegistry;
import ink.flybird.cubecraft.client.render.block.BlockRenderer;
import ink.flybird.cubecraft.client.render.block.LiquidRenderer;
import ink.flybird.cubecraft.client.render.gui.*;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.cubecraft.event.mod.ClientSideInitializeEvent;
import ink.flybird.cubecraft.event.mod.ModConstructEvent;
import ink.flybird.cubecraft.extension.CubecraftMod;
import ink.flybird.cubecraft.extension.ModSide;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

import static ink.flybird.cubecraft.client.gui.GUIRegistry.LAYOUT;
import static ink.flybird.cubecraft.client.gui.GUIRegistry.NODE;

@CubecraftMod(side = ModSide.CLIENT)
public final class CubecraftClientInternalMod {
    private static final ILogger LOGGER = LogManager.getLogger("client-internal-mod");

    @EventHandler
    public static void onClientSetup(ClientSetupEvent event) {
        event.getClient().getClientEventBus().registerEventListener(CubecraftClientInternalMod.class);
        LOGGER.info("detected client setup event :D");
    }

    @EventHandler
    public static void onClientDispose(ClientDisposeEvent event) {
        LOGGER.info("detected client dispose event :D");
    }

    @EventHandler
    public static void onModConstruct(ModConstructEvent event) {
        CubecraftClientInternalMod mod = event.getMod(CubecraftClientInternalMod.class);
        LOGGER.info("detected mod construct event :D (mod object=%s)", mod);
    }

    @EventHandler
    public static void onGUIContextPreInit(GUIContextPreInitEvent event) {
        GUIRegistry.registerRendererComponentPart(BorderImage.class, new BorderImage.JDeserializer());
        GUIRegistry.registerRendererComponentPart(HorizontalBoarderImage.class, new HorizontalBoarderImage.JDeserializer());
        GUIRegistry.registerRendererComponentPart(VerticalBorderImage.class, new VerticalBorderImage.JDeserializer());
        GUIRegistry.registerRendererComponentPart(Font.class, new Font.JDeserializer());
        GUIRegistry.registerRendererComponentPart(ImageAnimation.class, new ImageAnimation.JDeserializer());
        GUIRegistry.registerRendererComponentPart(Color.class, new Color.JDeserializer());

        GUIRegistry.registerComponent(Label.class);
        GUIRegistry.registerComponent(Button.class);
        GUIRegistry.registerComponent(Panel.class);
        GUIRegistry.registerComponent(Image.class);
        GUIRegistry.registerComponent(Icon.class);

        GUIRegistry.registerComponent(ScrollPanel.class);
        GUIRegistry.registerComponent(CardPanel.class);
        GUIRegistry.registerComponent(SplashText.class);
        GUIRegistry.registerComponent(TopBar.class);
        GUIRegistry.registerComponent(TextBar.class);
        GUIRegistry.registerComponent(ToggleButton.class);

        GUIRegistry.LAYOUT.registerItem(OriginLayout.class);
        GUIRegistry.LAYOUT.registerItem(ViewportLayout.class);
        GUIRegistry.LAYOUT.registerItem(FlowLayout.class);
    }


    @EventHandler
    public void registerClientMiscContent(ClientSideInitializeEvent e) {
        ClientRenderContext.WORLD_RENDERER.registerGetFunctionProvider(RenderRegistry.class);
        ClientRenderContext.CHUNK_LAYER_RENDERER.registerGetFunctionProvider(RenderRegistry.class);

        NODE.registerGetFunctionProvider(ink.flybird.cubecraft.client.registry.GUIRegistry.class);
        LAYOUT.registerGetFunctionProvider(GUIRegistry.class);


        ClientSharedContext.RESOURCE_MANAGER.registerEventListener(new ClientAssetLoader());
        ClientSharedContext.NET_HANDLER.registerGetFunctionProvider(ClientNetworkHandlerRegistry.class);
        ClientRenderContext.COLOR_MAP.registerGetter(ColorMapRegistry.class);
        CubecraftClient.CLIENT.getGuiManager().getEventBus().registerEventListener(new ScreenController());

        Object listener = new ClientListener();
        CubecraftClient.CLIENT.getClientEventBus().registerEventListener(listener);
        CubecraftClient.CLIENT.getClientEventBus().registerEventListener(new ParticleHandler());
        CubecraftClient.CLIENT.getDeviceEventBus().registerEventListener(listener);

        SharedContext.MOD.getModLoaderEventBus().registerEventListener(listener);
        ContentRegistries.ENTITY.registerItem(BlockBrakeParticle.class);


        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:calm_water", new LiquidRenderer(
                new TextureAsset("cubecraft:/block/water_still.png"),
                new TextureAsset("cubecraft:/block/water_flow.png")
        ));
        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:stone", new BlockRenderer(new TextureAsset("cubecraft", "/block/stone.png"), "cubecraft:alpha_block"));
    }
}
