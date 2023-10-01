package ink.flybird.cubecraft.client.internal;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.internal.gui.node.Button;
import ink.flybird.cubecraft.client.internal.gui.node.CardPanel;
import ink.flybird.cubecraft.client.internal.gui.node.TextBar;
import ink.flybird.cubecraft.client.internal.gui.node.WaitingAnimation;
import ink.flybird.cubecraft.client.internal.gui.node.Panel;
import ink.flybird.cubecraft.client.internal.gui.node.ScrollPanel;
import ink.flybird.cubecraft.client.internal.gui.node.TopBar;
import ink.flybird.cubecraft.client.internal.renderer.gui.*;
import ink.flybird.cubecraft.client.internal.entity.BlockBrakeParticle;
import ink.flybird.cubecraft.client.internal.handler.ClientListener;
import ink.flybird.cubecraft.client.internal.handler.ParticleHandler;
import ink.flybird.cubecraft.client.internal.handler.ResourceLoader;
import ink.flybird.cubecraft.client.internal.handler.ScreenController;
import ink.flybird.cubecraft.client.internal.registry.ClientNetworkHandlerRegistry;
import ink.flybird.cubecraft.client.internal.registry.ColorMapRegistry;
import ink.flybird.cubecraft.client.internal.registry.GUIRegistry;
import ink.flybird.cubecraft.client.internal.registry.RenderRegistry;
import ink.flybird.cubecraft.client.internal.renderer.block.BlockRenderer;
import ink.flybird.cubecraft.client.internal.renderer.block.LiquidRenderer;
import ink.flybird.cubecraft.client.internal.gui.layout.FlowLayout;
import ink.flybird.cubecraft.client.internal.gui.layout.OriginLayout;
import ink.flybird.cubecraft.client.internal.gui.layout.ViewportLayout;
import ink.flybird.cubecraft.client.resources.item.ImageResource;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.extansion.ExtensionSide;
import ink.flybird.cubecraft.mod.ClientSideInitializeEvent;
import ink.flybird.cubecraft.mod.ModPreInitializeEvent;
import ink.flybird.cubecraft.extansion.CubecraftExtension;
import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;

import static ink.flybird.cubecraft.client.gui.GUIRegistry.*;

@CubecraftExtension(side= ExtensionSide.CLIENT)
public final class ClientInternalMod {
    private final Logger logger = new SimpleLogger("ClientInternalMod");

    @EventHandler
    public void preInit(ModPreInitializeEvent e) {
        this.logger.info("initializing client internal mod...");
    }


    @EventHandler
    public void registerGUIComponents(ClientSideInitializeEvent e) {

        this.logger.info("registering gui component...");


        //guiManager.registerComponent("waiting", WaitingAnimation.class, new WaitingAnimation.XMLDeserializer());
        //guiManager.registerComponent("textbar", TextBar.class, new TextBar.XMLDeserializer());
        //guiManager.registerComponent("topbar", TopBar.class, new TopBar.XMLDeserializer());
        //guiManager.registerComponent("icon", Icon.class, new Icon.XMLDeserializer());

        //guiManager.registerComponent("panel", Panel.class, new Panel.XMLDeserializer());
        //guiManager.registerComponent("scroll_panel", ScrollPanel.class, new ScrollPanel.XMLDeserializer());
        //guiManager.registerComponent("card_panel", CardPanel.class, new CardPanel.XMLDeserializer());
    }

    @EventHandler
    public void registerGUILayout(ClientSideInitializeEvent e) {
        this.logger.info("registering gui layout...");
        GUIManager guiManager = CubecraftClient.CLIENT.getGuiManager();

        guiManager.registerLayout("origin", OriginLayout.class, new OriginLayout.XMLDeserializer());
        guiManager.registerLayout("flow", FlowLayout.class, new FlowLayout.XMLDeserializer());
        guiManager.registerLayout("viewport", ViewportLayout.class, new ViewportLayout.XMLDeserializer());
    }

    @EventHandler
    public void registerGUIRenderController(ClientSideInitializeEvent e) {
        this.logger.info("registering gui shortTick controller...");
        GUIManager guiManager = CubecraftClient.CLIENT.getGuiManager();

        guiManager.registerRenderController(Button.class, ResourceLocation.uiRenderController("cubecraft", "button.json"));
        guiManager.registerRenderController(Panel.class, ResourceLocation.uiRenderController("cubecraft", "panel.json"));
        guiManager.registerRenderController(TopBar.class, ResourceLocation.uiRenderController("cubecraft", "topbar.json"));
        guiManager.registerRenderController(TextBar.class, ResourceLocation.uiRenderController("cubecraft", "textbar.json"));
        guiManager.registerRenderController(WaitingAnimation.class, ResourceLocation.uiRenderController("cubecraft", "circlewaiting.json"));
        guiManager.registerRenderController(ScrollPanel.class, ResourceLocation.uiRenderController("cubecraft", "scroll_panel.json"));
        guiManager.registerRenderController(CardPanel.class, ResourceLocation.uiRenderController("cubecraft", "card_panel.json"));
    }

    @EventHandler
    public void registerGUIRendererComponent(ClientSideInitializeEvent e) {
        this.logger.info("registering gui renderer components");
        GUIManager guiManager = CubecraftClient.CLIENT.getGuiManager();

        guiManager.registerRendererPart("image_all_boarder", BoarderImage.class, new BoarderImage.JDeserializer());
        guiManager.registerRendererPart("image_horizontal_boarder", HorizontalBoarderImage.class, new HorizontalBoarderImage.JDeserializer());
        guiManager.registerRendererPart("image_vertical_boarder", VerticalBorderImage.class, new VerticalBorderImage.JDeserializer());
        guiManager.registerRendererPart("font", Font.class, new Font.JDeserializer());
        guiManager.registerRendererPart("image_animation", ImageAnimation.class, new ImageAnimation.JDeserializer());
        guiManager.registerRendererPart("color", Color.class, new Color.JDeserializer());



    }







    @EventHandler
    public void registerClientMiscContent(ClientSideInitializeEvent e) {
        ClientRenderContext.WORLD_RENDERER.registerGetFunctionProvider(RenderRegistry.class);
        ClientRenderContext.CHUNK_LAYER_RENDERER.registerGetFunctionProvider(RenderRegistry.class);

        NODE.registerGetFunctionProvider(GUIRegistry.class);
        LAYOUT.registerGetFunctionProvider(GUIRegistry.class);



        ClientSharedContext.RESOURCE_MANAGER.registerEventListener(new ResourceLoader());
        ClientSharedContext.NET_HANDLER.registerGetFunctionProvider(ClientNetworkHandlerRegistry.class);
        ClientRenderContext.COLOR_MAP.registerGetter(ColorMapRegistry.class);
        CubecraftClient.CLIENT.getGuiManager().getEventBus().registerEventListener(new ScreenController());

        Object listener = new ClientListener();
        CubecraftClient.CLIENT.getClientEventBus().registerEventListener(listener);
        CubecraftClient.CLIENT.getClientEventBus().registerEventListener(new ParticleHandler());
        CubecraftClient.CLIENT.getDeviceEventBus().registerEventListener(listener);

        SharedContext.MOD.getModLoaderEventBus().registerEventListener(listener);
        ContentRegistries.ENTITY.registerItem(BlockBrakeParticle.class);



        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:calm_water",new LiquidRenderer(
                new ImageResource("cubecraft:/texture/block/water_flow.png"),
                new ImageResource("cubecraft:/texture/block/water_still.png")
        ));
        ClientRenderContext.BLOCK_RENDERER.registerItem("cubecraft:stone",new BlockRenderer(new ImageResource("cubecraft","/texture/block/stone.png"), "cubecraft:alpha_block"));
    }
}
