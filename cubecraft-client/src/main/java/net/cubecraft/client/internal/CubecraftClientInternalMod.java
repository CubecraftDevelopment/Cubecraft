package net.cubecraft.client.internal;

import net.cubecraft.ContentRegistries;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.app.ClientDisposeEvent;
import net.cubecraft.client.event.app.ClientPostSetupEvent;
import net.cubecraft.client.event.app.ClientSetupEvent;
import net.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import net.cubecraft.client.gui.GUIRegistry;
import net.cubecraft.client.gui.layout.FlowLayout;
import net.cubecraft.client.gui.layout.OriginLayout;
import net.cubecraft.client.gui.layout.ViewportLayout;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.internal.handler.ClientAssetLoader;
import net.cubecraft.client.internal.handler.ClientListener;
import net.cubecraft.client.internal.handler.ParticleHandler;
import net.cubecraft.client.internal.handler.ScreenController;
import net.cubecraft.client.registry.ClientNetworkHandlerRegistry;
import net.cubecraft.client.registry.ColorMapRegistry;
import net.cubecraft.client.registry.RenderRegistry;
import net.cubecraft.client.render.block.BlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.world.WorldRendererRegistry;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.event.mod.ClientSideInitializeEvent;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.extension.CubecraftMod;
import net.cubecraft.extension.ModSide;
import net.cubecraft.util.NSStringDispatcher;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import net.cubecraft.client.gui.node.*;
import net.cubecraft.client.render.gui.*;

import static net.cubecraft.client.gui.GUIRegistry.LAYOUT;
import static net.cubecraft.client.gui.GUIRegistry.NODE;

@CubecraftMod(side = ModSide.CLIENT)
public final class CubecraftClientInternalMod {
    private static final ILogger LOGGER = LogManager.getLogger("client-internal-mod");

    @EventHandler
    public static void onClientSetup(ClientSetupEvent event) {
        ContentRegistries.EVENT_BUS.registerEventListener(ClientListener.class);
        event.getClient().getClientEventBus().registerEventListener(CubecraftClientInternalMod.class);

        ClientSharedContext.CLIENT_SETTING.register(ClientSettingRegistry.class);

        LOGGER.info("detected client setup event.");
    }

    @EventHandler
    public static void onClientPostSetup(ClientPostSetupEvent event) {
        for (String id : ContentRegistries.BLOCK.idList()) {
            String namespace = NSStringDispatcher.getNameSpace(id);
            String localId = NSStringDispatcher.getId(id);
            if (ClientRenderContext.BLOCK_RENDERER.get(id) != null) {
                continue;
            }
            ClientRenderContext.BLOCK_RENDERER.registerItem(id, new ModelBlockRenderer(new ModelAsset(namespace + ":/block/" + localId + ".json")));
        }
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
        ClientRenderContext.WORLD_RENDERER.registerFieldHolder(WorldRendererRegistry.class);

        ClientRenderContext.CHUNK_LAYER_RENDERER.registerGetFunctionProvider(RenderRegistry.class);

        NODE.registerGetFunctionProvider(net.cubecraft.client.registry.GUIRegistry.class);
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


        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:calm_water", new LiquidRenderer(new TextureAsset("cubecraft:/block/water_still.png"), new TextureAsset("cubecraft:/block/water_flow.png")));
        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:stone", new BlockRenderer(new TextureAsset("cubecraft", "/block/stone.png"), "cubecraft:alpha_block"));
    }
}
